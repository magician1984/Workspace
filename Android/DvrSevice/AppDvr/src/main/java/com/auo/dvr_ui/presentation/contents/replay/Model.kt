package com.auo.dvr_ui.presentation.contents.replay

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.CoroutineScope

class Model(
    private val onBack: () -> Unit,
    override val scope: CoroutineScope
) : Presenter.IModel<UiState, UserIntent, Effect> {
    private val _state: State<UiState> = mutableStateOf(UiState(
        groupList = emptyList(),
        selectedGroups = emptyList(),
        selectMode = false,
        displayType = UiState.DisplayType.All
    ))

    private val _effect: MutableState<Effect?> = mutableStateOf(null)

    override val state: State<UiState>
        get() = _state
    override val effect: State<Effect?>
        get() = _effect

    override fun handleUserIntent(intent: State<UserIntent>) {
        TODO("Not yet implemented")
    }

}