package com.auo.dvr_ui.presentation.contents.replay

import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.presentation.Presenter

data class UiState(val focusLocation : CamLocation?, val isPlaying : Boolean, val recordGroup : RecordGroup?) : Presenter.IUiState