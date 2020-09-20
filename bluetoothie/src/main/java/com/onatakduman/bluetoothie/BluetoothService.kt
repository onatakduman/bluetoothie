/*
 * Created by Onat AKDUMAN on 20.09.2020 18:44 , E-Mail : onatakduman@gmail.com
 * Last modified 20.09.2020 18:44
 *
 * Copyright (c)  2020.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.onatakduman.bluetoothie

import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Executors

/**
 * Created by douglas on 23/03/15.
 */
abstract class BluetoothService(config: BluetoothConfiguration) {

    // Debugging
    companion object {
        val TAG = this::class.java.simpleName
        val D = true

        @JvmStatic
        var mDefaultServiceInstance: BluetoothService? = null

        @JvmStatic
        var mDefaultConfiguration: BluetoothConfiguration? = null

        /**
         * Configures and initialize the BluetoothService singleton instance.
         *
         * @param config
         */
        @JvmStatic
        fun init(config: BluetoothConfiguration) {
            mDefaultConfiguration = config
            if (mDefaultServiceInstance != null) {
                mDefaultServiceInstance!!.stopService()
                mDefaultServiceInstance = null
            }
            try {
//            Constructor < ? extends BluetoothService > constructor =
//            (Constructor < ? extends BluetoothService >) mDefaultConfiguration . bluetoothServiceClass . getDeclaredConstructors ()[0]
                val constructor =
                    mDefaultConfiguration?.bluetoothServiceClass!!.declaredConstructors[0] as Constructor<out BluetoothService>
                constructor.isAccessible = true
                val bluetoothService =
                    constructor.newInstance(mDefaultConfiguration)
                mDefaultServiceInstance = bluetoothService
            } catch (e: InvocationTargetException) {
                throw  RuntimeException(e)
            } catch (e: InstantiationException) {
                throw  RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw  RuntimeException(e)
            }
        }

        /**
         * Get the BluetoothService singleton instance.
         *
         * @return
         */
        @JvmStatic
        @Synchronized
        fun getDefaultInstance(): BluetoothService {
            if (mDefaultServiceInstance == null) {
                throw IllegalStateException("BluetoothService is not initialized. Call BluetoothService.init(config).")
            }
            return mDefaultServiceInstance!!
        }
    }

    var mConfig: BluetoothConfiguration = config
    var mStatus: BluetoothStatus = BluetoothStatus.NONE
    var handler: Handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    var onEventCallback: OnBluetoothEventCallback? = null
    var onScanCallback: OnBluetoothScanCallback? = null

    fun getConfiguration(): BluetoothConfiguration = mConfig

    @Synchronized
    fun updateState(status: BluetoothStatus) {
        Log.v(TAG, "updateStatus() $mStatus -> $status")
        mStatus = status

        // Give the  state to the Handler so the UI Activity can update
        if (onEventCallback != null)
            executor.execute {
                onEventCallback?.onStatusChange(status)
            }
    }

    private fun runOnMainThread(runnable: Runnable, delayMillis: Long) {
        if (mConfig.callListenersInMainThread) {
            if (delayMillis > 0) {
                handler.postDelayed(runnable, delayMillis)
            } else {
                handler.post(runnable)
            }
        } else {
            if (delayMillis > 0) {
                Thread {
                    try {
                        Thread.sleep(delayMillis)
                        runnable.run()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                runnable.run()
            }
        }
    }

    protected fun runOnMainThread(runnable: Runnable) {
        runOnMainThread(runnable, 0)
    }

    protected fun removeRunnableFromHandler(runnable: Runnable) {
        handler.removeCallbacks(runnable)
    }

    /**
     * Current BluetoothService status.
     *
     * @return
     */
    @Synchronized
    fun getStatus(): BluetoothStatus = mStatus

    /**
     * Start scan process and call the {@link OnBluetoothScanCallback}
     */
    abstract fun startScan()

    /**
     * Stop scan process and call the {@link OnBluetoothScanCallback}
     */
    abstract fun stopScan()

    /**
     * Try to connect to the device and call the {@link OnBluetoothEventCallback}
     */
    abstract fun connect(device: BluetoothDevice)

    /**
     * Try to disconnect to the device and call the {@link OnBluetoothEventCallback}
     */
    abstract fun disconnect()

    /**
     * Write a array of bytes to the connected device.
     */
    abstract fun write(bytes: ByteArray?)

    /**
     * Stops the BluetoothService and turn it unusable.
     */
    abstract fun stopService()

    /**
     * Request the connection priority.
     */
    abstract fun requestConnectionPriority(connectionPriority: Int)

    /* ====================================
                STATICS METHODS
     ====================================== */

    /**
     * Use {@link BluetoothService#init(BluetoothConfiguration)} instead.
     *
     * @param config
     */
    @Deprecated(
        "setDefaultConfiguration(config: BluetoothConfiguration) is deprecated.",
        ReplaceWith("init(config)")
    )
    fun setDefaultConfiguration(config: BluetoothConfiguration) {
        init(config)
    }


}
