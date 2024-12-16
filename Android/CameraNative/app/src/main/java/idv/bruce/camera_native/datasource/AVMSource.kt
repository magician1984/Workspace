package idv.bruce.camera_native.datasource

import android.view.Surface
import com.auo.qcarcam.IQCarCamLib
import com.auo.qcarcam.QCarCamLibMock
import idv.bruce.camera_native.core.configure.AVMConfigure
import idv.bruce.camera_native.domain.datasource.IAVMSource

class AVMSource : IAVMSource {
    private val camLib : IQCarCamLib = QCarCamLibMock()

    private var callback : IAVMSource.OnStatusChangeListener? = null

    private var currentMode : Int = AVMConfigure.defaultPreviewMode.modeInd

    override fun attachSurface(surface: Surface) {
        camLib.init()
        camLib.attachSurface(surface)
        camLib.start()
        callback?.onChange(currentMode, null, null)
    }

    override fun detachSurface() {
        camLib.stop()
        camLib.detachSurface()
        camLib.release()
        callback?.onChange(null, null, null)
    }

    override fun switchMode(mode: Int) {
        currentMode = mode
        callback?.onChange(currentMode, null, null)
    }

    override fun registerOnStatusChangeListener(listener: IAVMSource.OnStatusChangeListener) {
        callback = listener
        callback?.onChange(currentMode, null, null)
    }
}
