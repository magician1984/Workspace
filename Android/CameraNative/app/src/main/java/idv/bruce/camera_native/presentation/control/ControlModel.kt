package idv.bruce.camera_native.presentation.control

import idv.bruce.camera_native.domain.usecase.IUseCaseSwitchMode
import idv.bruce.camera_native.domain.usecase.IUseCaseSyncStatus
import idv.bruce.camera_native.presentation.IModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ControlModel(private val switchMode: IUseCaseSwitchMode? = null, private val syncStatus: IUseCaseSyncStatus? = null) : IModel<ControlPageIntent, ControlPageState> {

    private val _state : MutableStateFlow<ControlPageState> = MutableStateFlow(ControlPageState())

    override val state: StateFlow<ControlPageState>
        get() = _state

    init {
        syncStatus?.invoke {
            _state.value = ControlPageState(it)
        }
    }

    override fun handleIntent(intent: ControlPageIntent) {
        when(intent){
            is ControlPageIntent.SwitchMode -> switchMode?.invoke(intent.previewMode)
        }
    }
}