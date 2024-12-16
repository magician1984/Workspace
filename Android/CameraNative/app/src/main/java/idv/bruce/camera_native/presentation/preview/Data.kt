package idv.bruce.camera_native.presentation.preview

import android.view.Surface
import idv.bruce.camera_native.domain.entity.AVMStatus
import idv.bruce.camera_native.presentation.UiState
import idv.bruce.camera_native.presentation.UserIntent

sealed class PreviewPageIntent : UserIntent{
    class StartPreview(val surface : Surface) : PreviewPageIntent()
    data object StopPreview : PreviewPageIntent()
}

data class PreviewPageState(val avmStatus: AVMStatus = AVMStatus.None) : UiState