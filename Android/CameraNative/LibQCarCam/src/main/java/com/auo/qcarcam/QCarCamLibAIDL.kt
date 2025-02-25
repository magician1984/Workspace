package com.auo.qcarcam

import android.os.IBinder
import android.os.ServiceManager
import android.util.Log
import android.view.Surface
import com.auo.qcarcam.exception.QCarCamException
import vendor.qti.automotive.qcarcam2.IQcarCamera
import vendor.qti.automotive.qcarcam2.IQcarCameraStream
import vendor.qti.automotive.qcarcam2.IQcarCameraStreamCB
import vendor.qti.automotive.qcarcam2.QCarCamEventPayload
import vendor.qti.automotive.qcarcam2.QCarCamInput
import vendor.qti.automotive.qcarcam2.QCarCamInputStream
import vendor.qti.automotive.qcarcam2.QCarCamOpenParam
import vendor.qti.automotive.qcarcam2.QCarCamOpmode

class QCarCamLibAIDL : IQCarCamLib {
    companion object {
        private const val TAG = "QCarCamLibAIDL"
        private const val SERVICE_NAME = "vendor.qti.automotive.qcarcam2.IQcarCamera/default"
    }

    class QCarCamLibAIDLException(msg: String = "Unknown error") : QCarCamException(msg)

    private var surface: Surface? = null

    private lateinit var service: IQcarCamera

    private var currentStream: IQcarCameraStream? = null

    private val streamCallback: IQcarCameraStreamCB = object : IQcarCameraStreamCB.Stub() {
        override fun qcarcam_event_callback(eventType: Int, p1: QCarCamEventPayload?) {
            Log.d(TAG, "Event callback: $eventType, $p1")
            when (eventType) {
                QCarCamEventPayload.u32Data -> Log.d(TAG, "Type: u32Data")
                QCarCamEventPayload.array -> Log.d(TAG, "Type: array")
                QCarCamEventPayload.errInfo -> Log.d(TAG, "Type: errInfo")
                QCarCamEventPayload.frameInfo -> Log.d(TAG, "Type: frameInfo")
                QCarCamEventPayload.hwTimestamp -> Log.d(TAG, "Type: hwTimestamp")
                QCarCamEventPayload.recovery -> Log.d(TAG, "Type: recovery")
                QCarCamEventPayload.vendorData -> Log.d(TAG, "Type: vendorData")
            }
        }

        override fun getInterfaceVersion(): Int {
            return service.interfaceVersion
        }

        override fun getInterfaceHash(): String {
            return service.interfaceHash
        }
    }

    init {
        Log.d(TAG, "Init: connect to service")
        val binder: IBinder = ServiceManager.getService(SERVICE_NAME)
            ?: throw QCarCamLibAIDLException("QCarCam service not found")
        service = IQcarCamera.Stub.asInterface(binder)
            ?: throw QCarCamLibAIDLException("Binder is not match")
        Log.d(TAG, "Init done.")
    }

    override fun attachSurface(surface: Surface) {
        Log.d(TAG, "Attaching surface")
        this.surface = surface
        try {
            startStreaming()
        } catch (e: QCarCamLibAIDLException) {
            Log.e(TAG, "Failed to start streaming: ${e.message}")
        }
    }

    override fun detachSurface() {
        this.surface = null
        try {
            stopStreaming()
        } catch (e: QCarCamLibAIDLException) {
            Log.e(TAG, "Failed to stop streaming: ${e.message}")
        }
    }

    override fun switchMode(mode: Int) {
    }

    override fun rotate(xAngle: Float) {
    }

    override fun getCurrentMode(): Int {
        return 0
    }

    override fun setCameraEventListener(listener: IQCarCamLib.CameraEventListener) {

    }

    @Throws(QCarCamLibAIDLException::class)
    private fun startStreaming() {
        Log.d(TAG, "Start streaming")

        if (currentStream != null)
            throw QCarCamLibAIDLException("Already streaming")

        val inputs: Array<QCarCamInput> = queryInputs()

        val input: QCarCamInput = inputs[0]

        currentStream = openInputStream(input)

        currentStream!!.startStream(streamCallback)
    }

    @Throws(QCarCamLibAIDLException::class)
    private fun stopStreaming() {
        Log.d(TAG, "Stop streaming")

        if (currentStream == null)
            throw QCarCamLibAIDLException("Not streaming")

        currentStream!!.stopStream()

        currentStream = null
    }

    private fun queryInputs(): Array<QCarCamInput> {
        Log.d(TAG, "Query inputs")

        val inputs: Array<QCarCamInput> = emptyArray()

        val errorCode = service.getInputStreamList(inputs)

        if (errorCode != 0)
            throw QCarCamLibAIDLException("Failed to query inputs: $errorCode")

        if (inputs.isEmpty())
            throw QCarCamLibAIDLException("No inputs found")

        return inputs
    }

    private fun openInputStream(input: QCarCamInput): IQcarCameraStream {
        Log.d(TAG, "Open input stream")

        val stream: QCarCamInputStream = QCarCamInputStream().apply {
            this.inputId = input.inputId
            this.srcId = 0
            this.inputMode = 0
        }

        val param: QCarCamOpenParam = QCarCamOpenParam().apply {
            this.inputs = arrayOf(stream)
            this.priority = 0
            this.numInputs = 1
            this.opMode = QCarCamOpmode.QCARCAM_OPMODE_RAW_DUMP
            this.flags = 0
        }

        return try {
            service.openStream(param)
        } catch (e: Exception) {
            throw QCarCamLibAIDLException("Failed to open stream: ${e.message}")
        }
    }
}