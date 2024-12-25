package idv.bruce.camera_native.datasource

import android.util.Log
import android.view.Surface
import com.auo.qcarcam.IQCarCamLib
import com.auo.qcarcam.QCarCamLibAIDL
import com.auo.qcarcam.QCarCamLibMock
import com.auo.qcarcam.exception.QCarCamException
import idv.bruce.camera_native.core.configure.AVMConfigure
import idv.bruce.camera_native.domain.datasource.IAVMSource

class AVMSource() : IAVMSource {
    companion object{
        private const val TAG = "AVMSource"
    }

    private val camLib : IQCarCamLib = try{
        Log.d(TAG, "Using AIDL")
        QCarCamLibAIDL()
    }catch (e : QCarCamException){
        Log.e(TAG, "Failed to create QCarCamLib instance: ${e.message}")
        QCarCamLibMock()
    }

    private var callback : IAVMSource.OnStatusChangeListener? = null

    private var currentMode : Int = AVMConfigure.defaultPreviewMode.modeInd


    init {
        camLib.setCameraEventListener { code, msg -> Log.d("AVMSource", "onEvent: $code, $msg") }
    }

    override fun attachSurface(surface: Surface) {
        camLib.attachSurface(surface)
        callback?.onChange(currentMode, null, null)
    }

    override fun detachSurface() {
        camLib.detachSurface()
        callback?.onChange(null, null, null)
    }

    override fun switchMode(mode: Int) {
        camLib.switchMode(mode)
        currentMode = camLib.getCurrentMode()
        callback?.onChange(currentMode, null, null)
    }

    override fun registerOnStatusChangeListener(listener: IAVMSource.OnStatusChangeListener) {
        callback = listener
        callback?.onChange(currentMode, null, null)
    }
}
