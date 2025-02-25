package idv.bruce.camera_native.presentation

import kotlinx.coroutines.flow.StateFlow

interface UserIntent

interface UiState

interface IModel<I : UserIntent, S : UiState>{
    val state : StateFlow<S>

    fun handleIntent(intent : I)
}