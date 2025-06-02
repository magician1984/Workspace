package com.auo.dvr_ui.presentation.contents.replay

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.flow.StateFlow

internal class View(
    state: StateFlow<UiState>,
    effect: StateFlow<Effect?>,
    intentHandler: (UserIntent) -> Unit
) : Presenter.IView<UiState, UserIntent, Effect>(state, effect, intentHandler) {
    @Composable
    override fun Draw(modifier: Modifier) {
    }
}