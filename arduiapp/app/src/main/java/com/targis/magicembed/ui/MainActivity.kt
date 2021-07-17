package com.targis.magicembed.ui

import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import com.targis.magicembed.device.BroadcastReceiverFactory
import com.targis.magicembed.di.AppContainer
import com.targis.magicembed.room.AppDatabase
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
