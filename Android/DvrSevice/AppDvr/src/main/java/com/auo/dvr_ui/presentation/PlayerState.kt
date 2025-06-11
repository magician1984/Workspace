package com.auo.dvr_ui.presentation

data class PlayerState(
    val isReady: Boolean,
    val isPlaying: Boolean,
    val position: Long,
    val duration: Long
)
