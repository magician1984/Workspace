package idv.bruce.camera_native.domain.entity

sealed class PreviewMode {
    data object AVM : PreviewMode()
    data object GRID : PreviewMode()
    class SINGLE(val index: Int) : PreviewMode()
}

sealed class ErrorType(val msg: String) {
    class CAMERA_FAIL(msg: String) : ErrorType(msg)
    class RENDERER_FAIL(msg: String) : ErrorType(msg)
}

sealed class AVMStatus {
    data object NONE : AVMStatus()
    data object READY : AVMStatus()
    class STREAMING(val previewMode: PreviewMode) : AVMStatus()
    class ERROR(val errorType: ErrorType) : AVMStatus()
}