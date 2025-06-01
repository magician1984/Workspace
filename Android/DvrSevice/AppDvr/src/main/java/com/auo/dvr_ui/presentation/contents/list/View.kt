package com.auo.dvr_ui.presentation.contents.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.auo.dvr_ui.presentation.Presenter

class View : Presenter.IView<UiState, UserIntent, Effect> {
    override val userIntent: State<UserIntent>
        get() = TODO("Not yet implemented")

    @Composable
    override fun Draw(modifier: Modifier, arg : Any?) {
        TODO("Not yet implemented")
    }

    override fun handleEffect(effect: State<Effect?>) {
        TODO("Not yet implemented")
    }

    override fun handleUiState(state: State<UiState>) {
        TODO("Not yet implemented")
    }
}