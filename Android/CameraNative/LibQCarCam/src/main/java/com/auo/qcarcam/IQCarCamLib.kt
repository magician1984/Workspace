package com.auo.qcarcam

import android.view.Surface
import com.auo.qcarcam.exception.QCarCamException

interface IQCarCamLib {
    fun interface CameraEventListener {
        fun onEvent(code: Int, msg: String)
    }

    @Throws(QCarCamException::class)
    fun attachSurface(surface: Surface)

    @Throws(QCarCamException::class)
    fun detachSurface()

    @Throws(QCarCamException::class)
    fun switchMode(mode : Int)

    @Throws(QCarCamException::class)
    fun rotate(xAngle: Float)

    @Throws(QCarCamException::class)
    fun getCurrentMode(): Int

    fun setCameraEventListener(listener: CameraEventListener)
}