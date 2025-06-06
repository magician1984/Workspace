package com.auo.dvr_ui.presentation.contents.replay

import android.net.Uri
import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.presentation.Presenter

data class UiState(
    val isPlaying: Boolean,
    val showThumbnail: Boolean,
    val thumbnails : Map<CamLocation, Uri>
) : Presenter.IUiState