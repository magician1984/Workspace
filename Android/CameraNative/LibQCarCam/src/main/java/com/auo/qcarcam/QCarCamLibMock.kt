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

    override fun attachSurface(surface: Surface) {
        onFunctionCall(surface)
    }

    override fun detachSurface() {
        onFunctionCall()
    }

    override fun switchMode(mode: Int) {
        onFunctionCall(mode)
    }

    override fun rotate(xAngle: Float) {
        onFunctionCall(xAngle)
    }

    override fun getCurrentMode(): Int {
        onFunctionCall()
        return 0
    }

    override fun setCameraEventListener(listener: IQCarCamLib.CameraEventListener) {
        onFunctionCall(listener)
    }

}