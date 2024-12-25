package com.auo.qcarcam

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.*
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import androidx.core.app.ActivityCompat
import com.auo.qcarcam.exception.QCarCamException
import java.util.concurrent.Executors


class QCarCamLibAndroid(private val context: Context) : IQCarCamLib {

    private var cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var previewSurface: Surface? = null
    private var cameraEventListener: IQCarCamLib.CameraEventListener? = null
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null
    private var currentMode: Int = 0
    private var cameraId: String? = null

    companion object {
        private const val TAG = "QCarCamLibAndroid"
        private const val CAMERA_REQUEST_CODE = 101
    }

    class QCarCamAndroidException(msg: String) : QCarCamException(msg)

    init {
        setupBackgroundThread()
        cameraId = getFirstCameraId()
    }

    private fun setupBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun getFirstCameraId(): String? {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                Log.d(TAG, "Camera ID: $cameraId")
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId
                }
            }
            return cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            return null
        } catch (e: ArrayIndexOutOfBoundsException) {
            e.printStackTrace()
            return null
        }
    }


    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startPreview()
            cameraEventListener?.onEvent(0, "Camera opened")
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
            cameraDevice = null
            cameraEventListener?.onEvent(1, "Camera disconnected")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
            cameraEventListener?.onEvent(2, "Camera error: $error")
        }
    }


    override fun attachSurface(surface: Surface) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw QCarCamAndroidException("Camera permission not granted")
        }
        previewSurface = surface
        try {
            cameraId?.let {
                cameraManager.openCamera(it, stateCallback, backgroundHandler)
            } ?: run {
                throw QCarCamAndroidException("No camera available")
            }
        } catch (e: CameraAccessException) {
            throw QCarCamAndroidException("Error opening camera: ${e.message}")
        }
    }


    override fun detachSurface() {
        closeCamera()
        previewSurface = null
    }

    private fun startPreview() {
        previewSurface?.let { surface ->
            cameraDevice?.let { camera ->
                try {
                    val captureRequestBuilder =
                        camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                            addTarget(surface)
                        }

                    val config: SessionConfiguration =
                        SessionConfiguration(
                            SessionConfiguration.SESSION_REGULAR,
                            listOf(OutputConfiguration(surface)),
                            Executors.newSingleThreadExecutor(),
                            object : CameraCaptureSession.StateCallback() {
                                override fun onConfigured(session: CameraCaptureSession) {
                                    captureSession = session
                                    updatePreview(captureRequestBuilder)
                                }

                                override fun onConfigureFailed(session: CameraCaptureSession) {
                                    cameraEventListener?.onEvent(
                                        3,
                                        "Failed to configure camera session"
                                    )
                                }
                            }
                        )
                    camera.createCaptureSession(config)

//                    camera.createCaptureSession(
//                        listOf(surface),
//                        object : CameraCaptureSession.StateCallback() {
//                            override fun onConfigured(session: CameraCaptureSession) {
//                                captureSession = session
//                                updatePreview(captureRequestBuilder)
//                            }
//
//                            override fun onConfigureFailed(session: CameraCaptureSession) {
//                                cameraEventListener?.onEvent(
//                                    3,
//                                    "Failed to configure camera session"
//                                )
//                            }
//                        },
//                        backgroundHandler
//                    )
                } catch (e: CameraAccessException) {
                    cameraEventListener?.onEvent(4, "Error starting preview: ${e.message}")
                }
            }
        }
    }

    private fun updatePreview(captureRequestBuilder: CaptureRequest.Builder) {
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        try {
            captureSession?.setRepeatingRequest(
                captureRequestBuilder.build(),
                null,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            cameraEventListener?.onEvent(5, "Error updating preview: ${e.message}")
        }
    }


    override fun switchMode(mode: Int) {
        currentMode = mode
        cameraEventListener?.onEvent(6, "Switched to mode: $mode")
    }

    override fun rotate(xAngle: Float) {
        cameraEventListener?.onEvent(7, "Rotated by: $xAngle")
    }

    override fun getCurrentMode(): Int {
        return currentMode
    }

    override fun setCameraEventListener(listener: IQCarCamLib.CameraEventListener) {
        cameraEventListener = listener
    }

    private fun closeCamera() {
        captureSession?.close()
        captureSession = null
        cameraDevice?.close()
        cameraDevice = null
    }

    fun release() {
        closeCamera()
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}