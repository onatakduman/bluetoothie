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

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.onatakduman.bluetoothie.*

class MainActivity : AppCompatActivity(), OnBluetoothEventCallback, OnBluetoothScanCallback {

    lateinit var blueService: BluetoothService
    var device = mutableListOf<BluetoothDeviceWrapper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        blueService = BluetoothService.getDefaultInstance()
        blueService.onEventCallback = this
        blueService.onScanCallback = this
        Handler(Looper.getMainLooper()).postDelayed({
            blueService.startScan()
            Handler(Looper.getMainLooper()).postDelayed({
                blueService.startScan()
            }, 500L)
        }, 500L)
    }

    fun writeData() {
        blueService.write("Write Something".toByteArray())
    }

    override fun onResume() {
        super.onResume()
        blueService.onEventCallback = this
    }

    override fun onDataRead(buffer: ByteArray, length: Int) {
        Log.e(BluetoothClassicService.TAG, "onToast(): ${String(buffer)}")
    }

    override fun onStatusChange(status: BluetoothStatus) {
        Log.e(BluetoothClassicService.TAG, "onToast(): ${status.name}")
    }

    override fun onDeviceName(deviceName: String) {
        Log.e(BluetoothClassicService.TAG, "onDeviceName(): $deviceName")
    }

    override fun onToast(message: String) {
        Log.e(BluetoothClassicService.TAG, "onToast(): $message")
    }

    override fun onDataWrite(buffer: ByteArray) {
        Log.e(BluetoothClassicService.TAG, "onDataWrite(): ${String(buffer)}")
    }

    override fun onDataTransfer(bytesWritten: Long, totalLength: Int) {
        val progress = bytesWritten * 100 / totalLength
        val progressText = "%$progress"
        // Updating UI
        if (progress == 0L || progress == 100L)
            Log.e(
                BluetoothClassicService.TAG,
                "onDataTransfer(): $progressText"
            )
    }

    override fun onDeviceDiscovered(deviceWrapper: BluetoothDeviceWrapper) {
        Log.e(BluetoothClassicService.TAG, "onDeviceDiscovered(): ${deviceWrapper.device.address}")
        device.add(deviceWrapper)
    }

    override fun onStartScan() {
        Log.e(BluetoothClassicService.TAG, "onStartScan()")
        // Update UI
    }

    override fun onStopScan() {
        Log.e(BluetoothClassicService.TAG, "onStopScan()")
        // Update UI
    }
}