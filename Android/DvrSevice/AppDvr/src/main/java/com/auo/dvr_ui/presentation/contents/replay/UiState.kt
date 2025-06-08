package com.auo.dvr_ui.presentation.contents.replay

import com.auo.dvr_ui.presentation.Presenter

data class UiState(
    val isPlaying: Boolean,
    val progress : Float
) : Presenter.IUiState