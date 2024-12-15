package idv.bruce.camera_native.presentation.control

import idv.bruce.camera_native.domain.usecase.IUseCaseSwitchMode
import idv.bruce.camera_native.presentation.IModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ControlModel(private val switchMode: IUseCaseSwitchMode? = null) : IModel<ControlPageIntent, ControlPageState> {

    private val _state : MutableStateFlow<ControlPageState> = MutableStateFlow(ControlPageState())

    override val state: StateFlow<ControlPageState>
        get() = _state

    override fun handleIntent(intent: ControlPageIntent) {

    }
}