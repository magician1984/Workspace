package com.auo.performancetester.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface UserIntent

interface UiState


interface IModel<I : UserIntent, S : UiState> : CoroutineScope{
    val state : StateFlow<S>

    fun handleIntent(intent : I)
}

