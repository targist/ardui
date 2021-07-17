package com.targist.magicembed.di

import android.hardware.usb.UsbManager
import com.targist.magicembed.device.AppBroadcastReceiver
import com.targist.magicembed.room.AppDatabase

data class AppContainer(
    val appDatabase: AppDatabase,
    val appBroadcastReceiver: AppBroadcastReceiver,
    val usbManager: UsbManager
)