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


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log
import androidx.annotation.RequiresPermission
import java.io.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class BluetoothClassicService private constructor(bluetoothConfiguration: BluetoothConfiguration) :
    BluetoothService(bluetoothConfiguration) {

    companion object {
        val TAG = BluetoothClassicService::class.java.simpleName
    }

    // Unique UUID for this application
    //private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private var mAdapter: BluetoothAdapter? = null
    private var mConnectThread: ConnectThread? = null
    private var mConnectedThread: ConnectedThread? = null

    init {
        mAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    @Synchronized
    override fun connect(device: BluetoothDevice) {
        if (D) Log.d(TAG, "connect to: $device")
        disconnect()

        // Start the thread to connect with the given device
        mConnectThread = ConnectThread(device)
        mConnectThread!!.start()
        updateState(BluetoothStatus.CONNECTING)
    }

    override fun disconnect() {
        if (D) Log.d(TAG, "disconnect")

        // Cancel any thread attempting to make a connection

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread!!.cancel()
            mConnectThread = null
        }

        // Cancel any thread currently running a connection

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread!!.cancel()
            mConnectedThread = null
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH)
    @Synchronized
    private fun connected(socket: BluetoothSocket, device: BluetoothDevice) {
        if (D) Log.d(TAG, "connected")

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = ConnectedThread(socket)
        mConnectedThread!!.start()

        // Send the name of the connected device back to the UI Activity
        if (onEventCallback != null) runOnMainThread { onEventCallback?.onDeviceName(device.name) }
        updateState(BluetoothStatus.CONNECTED)
    }

    override fun stopService() {
        if (D) Log.d(TAG, "stop")

        disconnect()

        if (mDefaultServiceInstance!! === this) mDefaultServiceInstance = null
    }

    @Synchronized
    override fun write(bytes: ByteArray?) {
        // Create temporary object

        // Create temporary object
        // Synchronize a copy of the ConnectedThread

        // Synchronize a copy of the ConnectedThread
        if (mStatus != BluetoothStatus.CONNECTED) return

        val r: ConnectedThread = mConnectedThread!!

        // Perform the write unsynchronized

        // Perform the write unsynchronized
        r.write(bytes!!)
    }


    private fun connectionFailed() {
        updateState(BluetoothStatus.NONE)

        // Send a failure message back to the Activity
        if (onEventCallback != null) runOnMainThread { onEventCallback?.onToast("Could not connect to device") }
    }

    private fun connectionLost() {
        updateState(BluetoothStatus.NONE)

        // Send a failure message back to the Activity
        if (onEventCallback != null) runOnMainThread { onEventCallback?.onToast("Connection lost") }
    }

    override fun requestConnectionPriority(connectionPriority: Int) {
        throw UnsupportedOperationException(
            "requestConnectionPriority is a feature in Bluetooth Low Energy " +
                    "and not supported in BluetoothClassic"
        )
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH])
    override fun startScan() {
        if (onScanCallback != null) runOnMainThread { onScanCallback?.onStartScan() }

        // Register for broadcasts when a device is discovered
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        mConfig.context?.registerReceiver(mScanReceiver, filter)

        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        mConfig.context!!.registerReceiver(mScanReceiver, filter)
        if (mAdapter!!.isDiscovering) {
            mAdapter!!.cancelDiscovery()
        }
        mAdapter!!.startDiscovery()
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH])
    override fun stopScan() {
        try {
            mConfig.context?.unregisterReceiver(mScanReceiver)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        if (mAdapter!!.isDiscovering) {
            mAdapter!!.cancelDiscovery()
        }
        if (onScanCallback != null) runOnMainThread { onScanCallback?.onStopScan() }
    }

    private val mScanReceiver = object : BroadcastReceiver() {
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH])
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val action = it.action

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND == action) {
                    // Get the BluetoothDevice object from the Intent
                    val device =
                        it.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    // If it's already paired, skip it, because it's been listed
                    // already
                    val RSSI = it.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    if (onScanCallback != null)
                        runOnMainThread {
                            onScanCallback?.onDeviceDiscovered(
                                BluetoothDeviceWrapper(
                                    device!!,
                                    RSSI.toInt()
                                )
                            )
                        }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                    stopScan()
                }
            }
        }
    }

    inner class ConnectThread @RequiresPermission(Manifest.permission.BLUETOOTH) constructor(
        private val mmDevice: BluetoothDevice
    ) :
        Thread() {
        private val mmSocket: BluetoothSocket?

        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN])
        override fun run() {
            Log.i(TAG, "BEGIN mConnectThread")
            name = "ConnectThread"

            // Always cancel discovery because it will slow down a connection
            if (mAdapter!!.isDiscovering) mAdapter!!.cancelDiscovery()

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket!!.connect()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                connectionFailed()
                // Close the socket
                try {
                    mmSocket!!.close()
                } catch (e2: java.lang.Exception) {
                    Log.e(
                        TAG,
                        "unable to close() socket during connection failure",
                        e2
                    )
                }
                return
            }

            // Reset the ConnectThread because we're done
            synchronized(this@BluetoothClassicService) { mConnectThread = null }

            // Start the connected thread
            connected(mmSocket, mmDevice)
        }

        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: Exception) {
                Log.e(TAG, "close() of connect socket failed", e)
            }
            try {
                interrupt()
            } catch (e: Exception) {
                Log.e(TAG, "interrupt() of Thread failed", e)
            }
        }

        init {
            var tmp: BluetoothSocket? = null

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(mConfig.uuid)
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "create() failed", e)
            }
            try {
                val mAudioManager =
                    mConfig.context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                //For phone speaker(loadspeaker)
                mAudioManager.mode = AudioManager.MODE_NORMAL
                mAudioManager.isBluetoothScoOn = false
                mAudioManager.isSpeakerphoneOn = true
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
            mmSocket = tmp
        }
    }

    /**
     * This thread runs during a connection with a remote device. It handles all incoming and outgoing transmissions.
     */
    inner class ConnectedThread constructor(socket: BluetoothSocket) : Thread() {
        private var mmSocket: BluetoothSocket? = null
        private var mmInStream: InputStream? = null
        private var mmOutStream: OutputStream? = null

        @Volatile
        private var canceled = false
        private lateinit var writeExecutor: Executor
        private lateinit var readExecutor: Executor

        init {
            mmSocket = socket
            try {
                mmInStream = mmSocket?.inputStream
                mmOutStream = mmSocket?.outputStream
                writeExecutor = Executors.newSingleThreadExecutor()
                readExecutor = Executors.newSingleThreadExecutor()
            } catch (e: Exception) {
                Log.e(TAG, "temp sockets not created", e)
            }
        }

        override fun run() {
            Log.i(TAG, "BEGIN mConnectedThread")
            var byteDelimiter = mConfig.characterDelimiter.toByte()
            val buffer = ByteArray(mConfig.bufferSize)
            var i = 0
            // Keep listening to the InputStream while connected
            while (!canceled) {
                try {
                    // Read from the InputStream
                    i = mmInStream!!.read(buffer)
                    dispatchBuffer(buffer, i)
                } catch (e: Exception) {
                    Log.e(TAG, "disconnected", e)
                    connectionLost()
                    break
                }
            }
        }

        private fun dispatchBuffer(buffer: ByteArray, i: Int) {
            val data = ByteArray(i)
            System.arraycopy(buffer, 0, data, 0, i)
            if (onEventCallback != null) {
                readExecutor.execute {
                    onEventCallback?.onDataRead(data, data.size)
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        fun write(buffer: ByteArray) {
            thread {
                if (buffer.size > 2048) {
                    val byteArrayInputStream = ByteArrayInputStream(buffer)
                    BufferedInputStream(byteArrayInputStream).use {
                        val bufferedOutputStream = BufferedOutputStream(mmOutStream)
                        try {
                            var sendBytes = 0L
                            var length: Int
                            val totalLength: Int = buffer.size
                            val bufferC = ByteArray(620)
                            length = it.read(bufferC)
                            while (length > -1) {
                                if (length > 0) {
                                    try {
                                        bufferedOutputStream.write(bufferC, 0, length)
                                        bufferedOutputStream.flush()
                                        sleep(7)
                                    } catch (e: IOException) {
                                        sleep(200)
                                        e.printStackTrace()
                                        break
                                    }
                                    sendBytes += length.toLong()
                                }
                                length = it.read(bufferC)
                                onEventCallback?.onDataTransfer(sendBytes, totalLength)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            byteArrayInputStream.close()
                        }
                    }
                } else {
                    mmOutStream!!.write(buffer)
                    mmOutStream!!.flush()
                    if (onEventCallback != null) {
                        onEventCallback?.onDataWrite(buffer)
                    }
                }
            }
        }

        fun cancel() {
            canceled = true
            try {
                mmSocket!!.close()
            } catch (e: Exception) {
                Log.e(TAG, "close() of connect socket failed", e)
            }
            try {
                mmInStream!!.close()
            } catch (e: Exception) {
                Log.e(TAG, "close() of connect socket failed", e)
            }
            try {
                interrupt()
            } catch (e: Exception) {
                Log.e(TAG, "interrupt() of Thread failed", e)
            }
        }
    }


}