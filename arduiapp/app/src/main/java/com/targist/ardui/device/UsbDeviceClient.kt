package com.targist.ardui.device

import android.hardware.usb.UsbManager
import androidx.compose.runtime.mutableStateListOf
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.targist.ardui.proto.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class DisposableUSBClient(
    private val coroutineScope: CoroutineScope,
    private val usbManager: UsbManager,
    private val driver: UsbSerialDriver
) {


    val logChannel = Channel<LogEntry>(40)
    val log = mutableStateListOf<LogEntry>()


    init {
        coroutineScope.launch(Dispatchers.Main) {
            logChannel.receiveAsFlow()
                .collect {
                    log.add(it)
                }

        }
    }

    private val disposeInstructions: MutableList<() -> Unit> = mutableListOf()
    lateinit var serialInputOutputManager: SerialInputOutputManager

    private fun internalRun() {

        val openDevice = usbManager.openDevice(driver.device)
        if (openDevice == null) {
            logChannel.addLogEntry(
                "Can not open device from manager ${driver.device}",
                LogType.APP_LOG
            )
            return
        }
        disposeInstructions.add { openDevice.close() }
        val port = driver.ports[0]
        if (port == null) {
            logChannel.addLogEntry(
                "Cannot open port to communicate with device ${driver.device}",
                LogType.APP_LOG
            )
            return
        }
        disposeInstructions.add { port.close() }
        port.open(openDevice)
        port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        logChannel.addLogEntry(
            "Connecting to usb device ${driver.device.deviceName}",
            LogType.APP_LOG
        )


        serialInputOutputManager =
            SerialInputOutputManager(port, object : SerialInputOutputManager.Listener {
                val buffer = StringBuffer()
                override fun onNewData(data: ByteArray?) {
                    data?.let { String(it) }
                        ?.forEach {
                            if(it == '\n'){
                                logChannel.addLogEntry(
                                    "received >> $buffer",
                                    LogType.READ
                                )
                                buffer.delete(0, buffer.length);
                            }
                            else {
                                buffer.append(it)
                            }
                        }


                }

                override fun onRunError(e: Exception?) {

                    logChannel.addLogEntry(
                        "read error>> : ${e?.stackTraceToString()}",
                        LogType.READ
                    )

                }
            }
            )

        serialInputOutputManager.writeBufferSize = 5000
        serialInputOutputManager.readBufferSize = 5000
        serialInputOutputManager.readTimeout = 5000

        serialInputOutputManager.start()
        disposeInstructions.add { serialInputOutputManager.stop() }

    }


    fun write(project: Project?) {
        if (project == null) return
        val program = project.program ?: return
        val data = program.encode()
        val size = data.size
        val buffer = byteArrayOf(size.toByte(), *data)

        try {
            serialInputOutputManager.writeAsync(buffer)
            logChannel.addLogEntry("sent >> $program", LogType.WRITE)

        } catch (e: Throwable) {
            logChannel.addLogEntry(e.stackTraceToString(), LogType.APP_LOG)
        }
    }


    fun run() {
        try {
            internalRun()
        } catch (e: Throwable) {
            logChannel.addLogEntry("Error while communicating with device $e", LogType.APP_LOG)
            onDispose()
        }
    }


    fun onDispose() {
        disposeInstructions.forEach {
            try {
                it()
            } catch (e: Throwable) {
                logChannel.addLogEntry("Error while disposing of resources $e", LogType.APP_LOG)
            }
        }

    }

}


fun Channel<LogEntry>.addLogEntry(text: String, logType: LogType) {
    this.trySend(LogEntry(text = text, logType = logType))
}