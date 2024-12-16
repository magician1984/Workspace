package com.auo.qcarcam

import android.util.Log
import android.view.Surface

class QCarCamLibMock : IQCarCamLib {
    companion object {
        private const val TAG: String = "QCarCamLib_Mock"

        private fun onFunctionCall(vararg values : Any) {
            val name = Thread.currentThread().stackTrace[4].methodName
            Log.d(TAG, "API [$name] called with values(${values.size})")
            for(value in values){
                Log.d(TAG, "Value : $value")
            }
        }
    }

    override fun init() {
        onFunctionCall()
    }

    override fun release() {
        onFunctionCall()
    }

    override fun attachSurface(surface: Surface) {
        onFunctionCall(surface)
    }

    override fun detachSurface() {
        onFunctionCall()
    }

    override fun start() {
        onFunctionCall()
    }

    override fun resume() {
        onFunctionCall()
    }

    override fun pause() {
        onFunctionCall()
    }

    override fun stop() {
        onFunctionCall()
    }

    override fun zoom(value: Float) {
        onFunctionCall(value)
    }

    override fun rotate(xAngle: Float, yAngle: Float) {
        onFunctionCall(xAngle, yAngle)
    }

    override fun setCameraEventListener(listener: IQCarCamLib.CameraEventListener) {
        onFunctionCall(listener)
    }

}