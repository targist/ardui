package com.targist.ardui.ui.serial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.targist.ardui.device.DisposableUSBClient
import com.targist.ardui.device.LogType
import com.targist.ardui.device.USBDeviceState
import com.targist.ardui.di.AppContainer
import com.targist.ardui.extension.toProject
import com.targist.ardui.proto.Project
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Composable
fun SerialView(
    appContainer: AppContainer,
) {

    val usbStatus: State<USBDeviceState> =
        produceState<USBDeviceState>(initialValue = USBDeviceState.Unknown, producer = {
            appContainer.appBroadcastReceiver.usbStateFlow
                .onEach {
                    value = it
                }
                .launchIn(this)
        })

    val usbDeviceState = usbStatus.value

    if (usbDeviceState !is USBDeviceState.DeviceConnected || usbDeviceState.drivers.isEmpty()) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "No device is connected")
        }
        return
    }
    val (selectedIndex, setSelectedIndex) = remember { mutableStateOf(0) }
    Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            val (expanded, setExpanded) = remember { mutableStateOf(false) }
            Text("Driver", modifier = Modifier.weight(1f), fontSize = 20.sp)
            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(Color.LightGray),
                onClick = { setExpanded(true) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            ) {
                val deviceName =
                    usbDeviceState.drivers[selectedIndex].device.deviceName
                Text(text = deviceName)

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { setExpanded(false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    usbDeviceState.drivers.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            onClick = {
                                setSelectedIndex(index)
                                setExpanded(false)
                            },
                        ) {
                            Text(text = s.device.deviceName)
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                )
            }
        }
        var project: Project? = null
        Row(verticalAlignment = Alignment.CenterVertically) {
            val (expanded, setExpanded) = remember { mutableStateOf(false) }
            val (selectedIndex, setSelectedIndex) = remember { mutableStateOf(0) }
            Text("Project", modifier = Modifier.weight(1f), fontSize = 20.sp)
            val projects = produceState<List<Project>>(emptyList()) {
                appContainer.appDatabase.projectDao().getAll()
                    .map {
                        it.map {
                            it.toProject()
                        }
                    }
                    .onEach {
                        value = it
                    }
                    .launchIn(this)
            }

            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(Color.LightGray),
                onClick = { setExpanded(true) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            ) {
                if (projects.value.isEmpty()) {
                    return@Button
                }
                project = projects.value[selectedIndex]
                Text(
                    text = if (projects.value.isEmpty()) "" else projects.value[selectedIndex].metadata?.name
                        ?: "Unknown"
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { setExpanded(false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    projects.value.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            onClick = {
                                setSelectedIndex(index)
                                setExpanded(false)
                            },
                        ) {
                            Text(text = s.metadata?.name ?: "Unknown")
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                )
            }
        }



        Text(text = "Logs", fontSize = 20.sp)

        val coroutineScope = rememberCoroutineScope()
        val disposableUsbClient = remember(key1 = usbStatus) {
            DisposableUSBClient(
                coroutineScope = coroutineScope,
                driver = usbDeviceState.drivers[selectedIndex],
                usbManager = appContainer.usbManager
            )
        }
        DisposableEffect(key1 = usbStatus) {
            disposableUsbClient.run()
            onDispose {
                disposableUsbClient.onDispose()
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            items(disposableUsbClient.log) {
                when (it.logType) {
                    LogType.READ -> Text(it.text, color = Color.Blue)
                    LogType.WRITE -> Text(text = it.text, color = Color.Magenta)
                    else -> Text(it.text)
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Button(onClick = { disposableUsbClient.write(project) }) {
                Text(text = "Upload")
            }
        }
    }
}