package idv.bruce.camera_native.presentation.preview

import idv.bruce.camera_native.domain.usecase.IUseCaseStartPreview
import idv.bruce.camera_native.domain.usecase.IUseCaseStopPreview
import idv.bruce.camera_native.presentation.IModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreviewModel(
    private val startPreview: IUseCaseStartPreview? = null,
    private val stopPreview: IUseCaseStopPreview? = null
) :
    IModel<PreviewPageIntent, PreviewPageState> {

    private val _state: MutableStateFlow<PreviewPageState> = MutableStateFlow(PreviewPageState())

    override val state: StateFlow<PreviewPageState>
        get() = _state

    override fun handleIntent(intent: PreviewPageIntent) {
        when (intent) {
            is PreviewPageIntent.StartPreview -> startPreview?.invoke(intent.surface)
            is PreviewPageIntent.StopPreview -> stopPreview?.invoke()
        }
    }
}