package com.targis.magicembed.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class AppBroadcastReceiver @AssistedInject constructor(
    private val manager: UsbManager,
    @Assisted private val lifecycleOwner: LifecycleOwner,
    @ApplicationContext private val context: Context
) : BroadcastReceiver(), LifecycleObserver {

    private val mutableState: MutableStateFlow<USBDeviceState> =
        MutableStateFlow(USBDeviceState.Unknown)

    val usbStateFlow = mutableState.asStateFlow()

    private val intentFilter = IntentFilter()


    init {
        lifecycleOwner.lifecycle.addObserver(this)
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)

        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            mutableState.value = USBDeviceState.DeviceDetached
        } else {
            mutableState.value = USBDeviceState.DeviceConnected(drivers = availableDrivers)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                mutableState.value = USBDeviceState.DeviceConnected(
                    drivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
                )
            }
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                mutableState.value = USBDeviceState.DeviceDetached
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        context.registerReceiver(this, intentFilter)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        context.unregisterReceiver(this)
    }


}