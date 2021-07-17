package com.targist.magicembed.ui.topbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.targist.magicembed.R
import com.targist.magicembed.device.USBDeviceState
import com.targist.magicembed.di.AppContainer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun MagicEmbedTopBar(
    openDrawer: () -> Unit,
    currentRoute: String,
    homeRoute: String,
    navigateToHome: () -> Unit,
    appContainer: AppContainer
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppBarHeight),
        color = Color.Transparent//MaterialTheme.colors.primarySurface
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (currentRoute == homeRoute) {
                IconButton(onClick = { openDrawer() }) {
                    Icon(
                        imageVector = Icons.Outlined.List,
                        contentDescription = "show menu",
                        Modifier
                            .padding(start = 20.dp)
                    )
                }
            } else {

                IconButton(onClick = { navigateToHome() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back to home",
                        Modifier
                            .padding(start = 20.dp)
                    )
                }

            }
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                )
            }


            val usbState =
                produceState<USBDeviceState>(initialValue = USBDeviceState.Unknown, producer = {
                    appContainer.appBroadcastReceiver.usbStateFlow.onEach {
                        value = it
                    }
                        .launchIn(this)
                })

            when (usbState.value) {
                is USBDeviceState.DeviceConnected -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_usb_black_24dp),
                        contentDescription = "show menu",
                        Modifier
                            .padding(end = 20.dp)
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_usb_off_black_24dp),
                        contentDescription = "show menu",
                        Modifier
                            .padding(end = 20.dp)
                    )
                }
            }

        }
    }
}


private val AppBarHeight = 56.dp