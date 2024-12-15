package idv.bruce.camera_native.domain.usecase

import android.view.Surface
import androidx.compose.runtime.State
import idv.bruce.camera_native.domain.entity.PreviewMode

interface IUseCaseSwitchMode{
    operator fun invoke(mode : PreviewMode)
}

interface IUseCaseStartPreview{
    operator fun invoke(surface : Surface)
}

interface IUseCaseStopPreview{
    operator fun invoke()
}

interface IUseCaseSyncMode{
    operator fun invoke() : State<PreviewMode>
}