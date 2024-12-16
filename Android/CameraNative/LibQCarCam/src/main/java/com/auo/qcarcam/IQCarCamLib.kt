package com.auo.qcarcam

import android.view.Surface
import com.auo.qcarcam.exception.QCarCamException

interface IQCarCamLib {
    fun interface CameraEventListener {
        fun onEvent(code: Int, msg: String)
    }

    @Throws(QCarCamException::class)
    fun init()

    @Throws(QCarCamException::class)
    fun release()

    @Throws(QCarCamException::class)
    fun attachSurface(surface: Surface)

    @Throws(QCarCamException::class)
    fun detachSurface()

    @Throws(QCarCamException::class)
    fun start()

    @Throws(QCarCamException::class)
    fun resume()

    @Throws(QCarCamException::class)
    fun pause()

    @Throws(QCarCamException::class)
    fun stop()

    @Throws(QCarCamException::class)
    fun zoom(value: Float)

    @Throws(QCarCamException::class)
    fun rotate(xAngle: Float, yAngle: Float)

    fun setCameraEventListener(listener: CameraEventListener)
}