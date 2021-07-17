package com.targist.magicembed.device

import com.hoho.android.usbserial.driver.UsbSerialDriver

sealed class USBDeviceState {
    class DeviceConnected(val drivers: List<UsbSerialDriver>) : USBDeviceState()
    object DeviceDetached : USBDeviceState()
    object Unknown : USBDeviceState()
}

