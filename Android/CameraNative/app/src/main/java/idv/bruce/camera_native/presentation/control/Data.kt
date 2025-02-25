package idv.bruce.camera_native.presentation.control

import idv.bruce.camera_native.domain.entity.AVMStatus
import idv.bruce.camera_native.domain.entity.PreviewMode
import idv.bruce.camera_native.presentation.UiState
import idv.bruce.camera_native.presentation.UserIntent

sealed class ControlPageIntent : UserIntent{
    class SwitchMode(val previewMode: PreviewMode) : ControlPageIntent()
}

data class ControlPageState(val avmStatus: AVMStatus = AVMStatus.None) : UiState