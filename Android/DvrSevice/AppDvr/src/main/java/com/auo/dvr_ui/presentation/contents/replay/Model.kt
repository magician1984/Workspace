package com.auo.dvr_ui.presentation.contents.replay

import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

internal class Model(scope: CoroutineScope,
) : Presenter.IModel<UiState, UserIntent, Effect>(scope) {
    override val state: StateFlow<UiState>
        get() = TODO("Not yet implemented")
    override val effect: StateFlow<Effect?>
        get() = TODO("Not yet implemented")

    override fun handleUserIntent(intent: UserIntent) {
        TODO("Not yet implemented")
    }

}