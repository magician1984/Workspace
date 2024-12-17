package com.auo.qcarcam

import android.view.Surface

class QCarCamLibImpl : IQCarCamLib {
    companion object {
        init {
            System.loadLibrary("qcarcam_jni")
        }
    }

    override fun attachSurface(surface: Surface) {
        nativeAttachSurface(surface)
    }

    override fun detachSurface() {
        nativeDetachSurface()
    }

    override fun switchMode(mode: Int) {
        nativeSwitchMode(mode)
    }

    override fun rotate(xAngle: Float) {
        nativeRotate(xAngle)
    }

    override fun getCurrentMode() : Int = nativeGetCurrentMode()

    override fun setCameraEventListener(listener: IQCarCamLib.CameraEventListener) {
        TODO("Not yet implemented")
    }

    private fun onNativeEvent(code: Int, msg: String) {

    }

    private external fun nativeAttachSurface(surface: Surface): Int

    private external fun nativeDetachSurface(): Int

    private external fun nativeSwitchMode(mode: Int): Int

    private external fun nativeRotate(angle : Float) : Int

    private external fun nativeGetCurrentMode() : Int

}