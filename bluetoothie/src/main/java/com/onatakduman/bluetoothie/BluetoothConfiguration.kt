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
import android.content.Context
import android.os.Build
import android.util.Log
import java.util.*


/**
 * Created by dougl on 10/04/2017.
 */
class BluetoothConfiguration {
    /**
     * Class reference for the [BluetoothService] implementation.
     *
     * @see BluetoothClassicService
     */
    var bluetoothServiceClass: Class<out BluetoothService?>? = null

    /**
     * [android.app.Application] context reference.
     */
    var context: Context? = null

    /**
     * Name of your application or device.
     */
    var deviceName: String? = null
    var bufferSize = 0
    var characterDelimiter = 0.toChar()
    var uuid: UUID? = null
    var uuidService: UUID? = null
    var uuidCharacteristic: UUID? = null
    var packageSize = 620
    var useDelayBetweenPackages = false
    var delayBetweenPackages = 10L
    var useDelayPerPackages = false
    var perPackageSize = 10
    var perPackageDelay = 10L

    /**
     * Preferred transport for GATT connections to remote dual-mode devices
     * [BluetoothDevice.TRANSPORT_AUTO] or
     * [BluetoothDevice.TRANSPORT_BREDR] or [BluetoothDevice.TRANSPORT_LE]
     */
    var transport = 0

    /**
     * Whether to call the listener only in Main Thread (true)
     * or call in the Thread where the event occurs (false).
     */
    var callListenersInMainThread = true

    /**
     * Request a specific connection priority. Must be one of
     * [BluetoothGatt.CONNECTION_PRIORITY_BALANCED], [BluetoothGatt.CONNECTION_PRIORITY_HIGH]
     * or [BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER].
     *
     * An application should only request high priority connection parameters to transfer
     * large amounts of data over LE quickly. Once the transfer is complete, the application
     * should request [BluetoothGatt.CONNECTION_PRIORITY_BALANCED] connection parameters
     * to reduce energy use.
     */
    var connectionPriority = 0

    /**
     * Set the default value for [BluetoothConfiguration.transport].
     */
    private fun setDefaultTransport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            transport = BluetoothDevice.TRANSPORT_LE
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // From Android LOLLIPOP (21) the transport types exists, but them are hide for use,
            // so is needed to use relfection to get the value
            try {
                transport =
                    BluetoothDevice::class.java.getDeclaredField("TRANSPORT_LE").getInt(null)
            } catch (ex: Exception) {
                Log.d(TAG, "Error on get BluetoothDevice.TRANSPORT_LE with reflection.", ex)
            }
        } else {
            transport = -1
        }
    }

    companion object {
        private val TAG = BluetoothConfiguration::class.java.simpleName
    }

    init {
        setDefaultTransport()
    }
}