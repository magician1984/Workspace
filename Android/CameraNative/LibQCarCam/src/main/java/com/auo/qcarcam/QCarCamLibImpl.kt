package com.auo.qcarcam

import android.util.Log
import android.view.Surface
import com.auo.qcarcam.exception.QCarCamException

class QCarCamLibImpl : IQCarCamLib {

    companion object{
        private var libLoaded : Boolean = false
    }

    inner class NativeLibLoadFailedException : QCarCamException("Native Lib Load Failed")

    init {
        try{
            System.loadLibrary("qcarcam_jni")
            libLoaded = true
        }catch (e : SecurityException){
            Log.e("QCarCamLibImpl", "Load Library Failed: ${e.message}")
            throw NativeLibLoadFailedException()
        }catch (e : UnsatisfiedLinkError){
            Log.e("QCarCamLibImpl", "Load Library Failed: ${e.message}")
            throw NativeLibLoadFailedException()
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