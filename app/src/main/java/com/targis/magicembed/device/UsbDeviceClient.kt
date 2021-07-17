package com.targis.magicembed.device

import android.hardware.usb.UsbManager
import androidx.compose.runtime.mutableStateListOf
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.targist.magicembed.proto.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DisposableUSBClient(
    private val coroutineScope: CoroutineScope,
    private val usbManager: UsbManager,
    private val driver: UsbSerialDriver
) {
    val log = mutableStateListOf<LogEntry>()


    private val disposeInstructions: MutableList<() -> Unit> = mutableListOf()
    lateinit var serialInputOutputManager: SerialInputOutputManager

    private fun internalRun() {
        val openDevice = usbManager.openDevice(driver.device)
        if (openDevice == null) {
            log.addLogEntry("Can not open device from manager ${driver.device}", LogType.APP_LOG)
            return
        }
        disposeInstructions.add { openDevice.close() }
        val port = driver.ports[0]
        if (port == null) {
            log.addLogEntry(
                "Cannot open port to communicate with device ${driver.device}",
                LogType.APP_LOG
            )
            return
        }
        disposeInstructions.add { port.close() }
        port.open(openDevice)
        port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        log.addLogEntry("Connecting to usb device ${driver.device.deviceName}", LogType.APP_LOG)


        serialInputOutputManager =
            SerialInputOutputManager(port, object : SerialInputOutputManager.Listener {
                override fun onNewData(data: ByteArray?) {
                    runInUiThread {
                        log.addLogEntry(
                            "received " + data?.let { String(it) },
                            LogType.READ)
                    }

                }

                override fun onRunError(e: Exception?) {
                    runInUiThread {
                        log.addLogEntry(">> error  read : ${e?.stackTraceToString()}", LogType.READ)
                    }
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
            log.addLogEntry("write data: " + program.toString(), LogType.WRITE)
        } catch (e: Throwable) {
            log.addLogEntry(e.stackTraceToString(), LogType.APP_LOG)
        }
    }


    fun run() {
        try {
            internalRun()
        } catch (e: Throwable) {
            log.addLogEntry("Error while communicating with device $e", LogType.APP_LOG)
            onDispose()
        }
    }


    fun onDispose() {
        disposeInstructions.forEach {
            try {
                it()
            } catch (e: Throwable) {
                log.addLogEntry("Error while disposing of resources $e", LogType.APP_LOG)
            }
        }

    }

    fun runInUiThread(block: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch(Dispatchers.Main) {
            this.block()
        }
    }
}


enum class LogType {
    WRITE, READ, APP_LOG
}

data class LogEntry(val text: String, val logType: LogType)


fun MutableList<LogEntry>.addLogEntry(text: String, logType: LogType) {
    this.add(LogEntry(text = text, logType = logType))
}