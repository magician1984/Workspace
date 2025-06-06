package com.auo.dvr_ui.presentation.contents.replay

import android.view.SurfaceHolder
import android.view.TextureView
import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.presentation.Presenter

sealed class UserIntent : Presenter.IUserIntent{
    data class SelectCamera(val location : CamLocation) : UserIntent()
    data object PlayStateSwitch : UserIntent()
    data object Previous : UserIntent()
    data object Next : UserIntent()
    data class SeekTo(val time : Long) : UserIntent()
    data class SurfaceReady(val list : List<Pair<CamLocation, SurfaceHolder>>) : UserIntent()
    data object Back : UserIntent()

}