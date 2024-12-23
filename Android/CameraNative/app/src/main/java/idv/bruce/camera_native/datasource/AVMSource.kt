package idv.bruce.camera_native.datasource

import android.content.Context
import android.view.Surface
import com.auo.qcarcam.IQCarCamLib
import com.auo.qcarcam.QCarCamLibAndroid
import com.auo.qcarcam.QCarCamLibImpl
import com.auo.qcarcam.QCarCamLibMock
import com.auo.qcarcam.exception.QCarCamException
import idv.bruce.camera_native.core.configure.AVMConfigure
import idv.bruce.camera_native.domain.datasource.IAVMSource

class AVMSource(context: Context) : IAVMSource {
    private val camLib : IQCarCamLib = try{
        QCarCamLibImpl(context)
    }catch (e : QCarCamException){
        QCarCamLibMock()
    }

    private var callback : IAVMSource.OnStatusChangeListener? = null

    private var currentMode : Int = AVMConfigure.defaultPreviewMode.modeInd

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
