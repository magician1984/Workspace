package idv.bruce.camera_native.domain.entity

import idv.bruce.camera_native.core.configure.AVMConfigure

sealed class PreviewMode(val modeInd : Int) {
    data object Avm : PreviewMode(AVMConfigure.CameraModeCode.AVM)
    data object Grid : PreviewMode(AVMConfigure.CameraModeCode.GRID)
    class Single(index: Int) : PreviewMode(index)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PreviewMode

        return modeInd == other.modeInd
    }

    override fun hashCode(): Int {
        return modeInd
    }
}

sealed class ErrorType(val msg: String) {
    class CameraError(msg: String) : ErrorType(msg)
    class RendererError(msg: String) : ErrorType(msg)
}

sealed class AVMStatus {
    data object None : AVMStatus()
    class Streaming(val previewMode: PreviewMode) : AVMStatus()
    class Error(val errorType: ErrorType) : AVMStatus()
}