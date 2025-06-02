package com.auo.dvr_ui.presentation.contents.replay

import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.presentation.Presenter

sealed class UserIntent : Presenter.IUserIntent{
    data class SelectCamera(val location : CamLocation) : UserIntent()
    data object Start : UserIntent()
    data object Pause : UserIntent()
    data object Stop : UserIntent()
    data class SeekTo(val time : Long) : UserIntent()
}