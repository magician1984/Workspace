package idv.bruce.camera_native.domain.usecase

import android.view.Surface
import idv.bruce.camera_native.domain.entity.AVMStatus
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

interface IUseCaseSyncStatus{
    operator fun invoke(callback : (AVMStatus)->Unit)
}