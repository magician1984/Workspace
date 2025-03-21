package com.auo.dvr_ui.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

internal interface UserIntent

internal interface UiState

internal interface IModel<I : UserIntent, S : UiState> : CoroutineScope{
    val state : StateFlow<S>

    fun handleIntent(intent : I)
}

