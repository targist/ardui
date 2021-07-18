package com.targist.ardui.di

import android.hardware.usb.UsbManager
import com.targist.ardui.device.AppBroadcastReceiver
import com.targist.ardui.room.AppDatabase

data class AppContainer(
    val appDatabase: AppDatabase,
    val appBroadcastReceiver: AppBroadcastReceiver,
    val usbManager: UsbManager
)