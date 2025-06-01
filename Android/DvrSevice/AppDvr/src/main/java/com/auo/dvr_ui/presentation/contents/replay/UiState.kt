package com.auo.dvr_ui.presentation.contents.replay

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.presentation.Presenter

data class UiState(
    val groupList: List<RecordGroup>,
    val selectedGroups: List<RecordGroup>,
    val selectMode : Boolean,
    val displayType : DisplayType,
) : Presenter.IUiState{
    enum class DisplayType{
        All,
        Event,
        Locked
    }
}