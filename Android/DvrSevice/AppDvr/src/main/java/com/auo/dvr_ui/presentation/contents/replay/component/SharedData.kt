package com.auo.dvr_ui.presentation.contents.replay.component



data class SharedData(val playState: PlayState, val time: Long) {
    enum class PlayState{
        Idle,
        Playing,
        Pause,
        Stop,
        Seeking
    }
}