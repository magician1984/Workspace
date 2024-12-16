package com.auo.qcarcam

import android.view.Surface

class QCarCamLibImpl : IQCarCamLib {
    companion object{
        init {
            System.loadLibrary("qcarcam_jni")
        }
    }

    override fun init() {
        nativeInit()
    }

    override fun release() {
        nativeRelease()
    }

    override fun attachSurface(surface: Surface) {
        nativeAttachSurface(surface)
    }

    override fun detachSurface() {
        nativeDetachSurface()
    }

    override fun start() {
        nativeStart()
    }

    override fun resume() {
        nativeResume()
    }

    override fun pause() {
        nativePause()
    }

    override fun stop() {
        nativeStop()
    }

    override fun zoom(value: Float) {
        TODO("Not yet implemented")
    }

    override fun rotate(xAngle: Float, yAngle: Float) {
        TODO("Not yet implemented")
    }

    override fun setCameraEventListener(listener: IQCarCamLib.CameraEventListener) {
        TODO("Not yet implemented")
    }

    private fun onNativeEvent(code : Int, msg: String){

    }

    private external fun nativeInit() : Int

    private external fun nativeRelease() : Int

    private external fun nativeAttachSurface(surface: Surface): Int

    private external fun nativeDetachSurface():Int

    private external fun nativeStart():Int

    private external fun nativeResume():Int

    private external fun nativePause():Int

    private external fun nativeStop():Int
}