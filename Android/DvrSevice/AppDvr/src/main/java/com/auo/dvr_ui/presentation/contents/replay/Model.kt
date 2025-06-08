package com.auo.dvr_ui.presentation.contents.replay

import android.view.SurfaceHolder
import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.presentation.GlobalState
import com.auo.dvr_ui.presentation.PlayerState
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class Model(
    scope: CoroutineScope,
    globalState: StateFlow<GlobalState>,
    private val playerState: StateFlow<PlayerState>,
    private val onViewReady: (List<Pair<CamLocation, SurfaceHolder>>) -> Unit,
    private val onPlayRequest: () -> Unit,
    private val onPauseRequest: () -> Unit,
    private val onNextRequest: () -> Unit,
    private val onPrevRequest: () -> Unit,
    private val onBackRequest: () -> Unit,
) : Presenter.IModel<UiState, UserIntent, Effect>(scope, globalState) {
    private val _state: MutableStateFlow<UiState> = MutableStateFlow(
        UiState(isPlaying = false, progress = 0f)
    )

    private val _effect: MutableStateFlow<Effect?> = MutableStateFlow(null)

    override val state: StateFlow<UiState>
        get() = _state

    override val effect: StateFlow<Effect?>
        get() = _effect

    init {
        scope.launch {
            playerState.map { it.isPlaying }.distinctUntilChanged().collect {
                _state.update { state ->
                    state.copy(isPlaying = it)
                }
            }
            playerState.map { it.position to it.duration }
                .distinctUntilChanged().collect {
                    _state.update { state ->
                        state.copy(progress = it.first.toFloat() / it.second.toFloat())
                    }
                }
        }
    }

    override fun handleUserIntent(intent: UserIntent) {
        scope.launch {
            when (intent) {
                is UserIntent.SurfaceReady -> ::onViewReady
                is UserIntent.SeekTo -> TODO()
                UserIntent.Back -> ::onBackRequest
                UserIntent.Next -> ::onNextRequest
                UserIntent.PlayStateSwitch -> if (playerState.value.isPlaying) ::onPauseRequest else ::onPlayRequest
                UserIntent.Previous -> ::onPrevRequest
            }
        }
    }
}