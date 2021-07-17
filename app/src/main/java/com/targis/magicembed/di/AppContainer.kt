package com.targis.magicembed.di

import android.hardware.usb.UsbManager
import com.targis.magicembed.device.AppBroadcastReceiver
//import com.targis.magicembed.device.UsbDeviceClient
import com.targis.magicembed.room.AppDatabase

data class AppContainer(
    val appDatabase: AppDatabase,
    val appBroadcastReceiver: AppBroadcastReceiver,
    val usbManager: UsbManager
)