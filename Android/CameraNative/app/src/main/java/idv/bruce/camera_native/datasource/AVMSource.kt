package idv.bruce.camera_native.datasource

import android.view.Surface
import com.auo.qcarcam.IQCarCamLib
import com.auo.qcarcam.QCarCamLibImpl
import com.auo.qcarcam.QCarCamLibMock
import idv.bruce.camera_native.core.configure.AVMConfigure
import idv.bruce.camera_native.domain.datasource.IAVMSource

class AVMSource : IAVMSource {
    private val camLib : IQCarCamLib = QCarCamLibImpl()

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
