package com.targist.magicembed.ui

import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import com.targist.magicembed.device.BroadcastReceiverFactory
import com.targist.magicembed.di.AppContainer
import com.targist.magicembed.room.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appDatabase: AppDatabase
    @Inject
    lateinit var broadcastReceiverFactory: BroadcastReceiverFactory
    @Inject
    lateinit var usbManager: UsbManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val broadcastReceiver = broadcastReceiverFactory.create(this)
        val appContainer = AppContainer(appDatabase, broadcastReceiver, usbManager)
        setContent {
            MagicEmbedApp(appContainer)
        }
    }
}
