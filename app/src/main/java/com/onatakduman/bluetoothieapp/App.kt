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

package com.onatakduman.bluetoothieapp

import android.app.Application
import com.onatakduman.bluetoothie.BluetoothClassicService
import com.onatakduman.bluetoothie.BluetoothConfiguration
import com.onatakduman.bluetoothie.BluetoothService
import java.util.*

class App : Application() {
    val BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    override fun onCreate() {
        super.onCreate()
        val config = BluetoothConfiguration()

        config.bluetoothServiceClass = BluetoothClassicService::class.java
        config.context = applicationContext
        config.bufferSize = 1024
        config.characterDelimiter = '\n'
        config.deviceName = "Bluetooth Sample"
        config.callListenersInMainThread = true

        config.packageSize = 620 //
        config.useDelayBetweenPackages = true
        config.delayBetweenPackages = 10L // max 50L
        config.useDelayPerPackages = true
        config.perPackageSize = 10 // every 10 package sent use perPackageDelay
        config.perPackageDelay = 15L // max 50L

        config.uuid = BLUETOOTH_SPP // For Classic
        BluetoothService.init(config)
    }
}