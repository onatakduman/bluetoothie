## Bluetoothie
![Logo](https://github.com/onatakduman/bluetoothie/blob/master/art/bluetoothie_logo.png)
[![](https://jitpack.io/v/onatakduman/bluetoothie.svg)](https://jitpack.io/#onatakduman/bluetoothie)

While I was preparing Bluetoothie, I was inspired by [douglasjunior/AndroidBluetoothLibrary](https://github.com/douglasjunior/AndroidBluetoothLibrary). It likely similar in most cases, 
I jus change some data reading buffer. It looks like Kotlin version of [this](https://github.com/douglasjunior/AndroidBluetoothLibrary) library.
1. [douglasjunior/AndroidBluetoothLibrary](https://github.com/douglasjunior/AndroidBluetoothLibrary) 's data read function is buffers 1024 bytes and does not triger read function until its full.
2. [glodanif/BluetoothChat](https://github.com/glodanif/BluetoothChat) I inspired file transfer progress method.

Installation
------
1. Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

2. Add the dependency
```gradle
dependencies {
    implementation 'com.github.onatakduman:bluetoothie:0.0.1'
}
```

3. Add Permissions to 'AndroidManifest.xml'
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android" >
  <uses-permission android:name="android.permission.BLUETOOTH" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
  <uses-permission android:name="android.permission.BLUETOOTH_PRIVILEGED" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
  ...
</manifest>
```

How to Use
------
See the [Sample Application](https://github.com/onatakduman/bluetoothie/tree/master/app/src/main/java/com/onatakduman/bluetoothieapp) for use. 

Update Checklist
------
- [x] Bluetooth Support
- [ ] BLE Support
- [ ] ViewModel usage example
- [ ] More description of usage


Reference Libraries
------
1. [douglasjunior/AndroidBluetoothLibrary](https://github.com/douglasjunior/AndroidBluetoothLibrary)
2. [glodanif/BluetoothChat](https://github.com/glodanif/BluetoothChat)


Researched BLE Libraries
------
1. [Jasonchenlijian/FastBle](https://github.com/Jasonchenlijian/FastBle)
2. [Polidea/RxAndroidBle](https://github.com/Polidea/RxAndroidBle)
3. [NordicSemiconductor/Android-BLE-Library](https://github.com/NordicSemiconductor/Android-BLE-Library)
4. [kai-morich/SimpleBluetoothLeTerminal](https://github.com/kai-morich/SimpleBluetoothLeTerminal)
4. [Lembed/Android-BLE-Terminal](https://github.com/Lembed/Android-BLE-Terminal)

Researched Bluetooth Serial Libraries
------
1. [IvBaranov/RxBluetooth](https://github.com/IvBaranov/RxBluetooth)
2. [OmarAflak/Bluetooth-Library](https://github.com/OmarAflak/Bluetooth-Library)
3. [kai-morich/SimpleBluetoothTerminal](https://github.com/kai-morich/SimpleBluetoothTerminal)
4. [harry1453/android-bluetooth-serial](https://github.com/harry1453/android-bluetooth-serial)
5. [ThanosFisherman/BlueFlow](https://github.com/ThanosFisherman/BlueFlow)

Licence
------
```
MIT License

Copyright (c) 2020 Onat AKDUMAN

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```