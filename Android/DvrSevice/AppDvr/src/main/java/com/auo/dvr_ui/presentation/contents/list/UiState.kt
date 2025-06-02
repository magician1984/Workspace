package com.auo.dvr_ui.presentation.contents.list

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.presentation.Presenter

data class UiState(
    val groupList: List<RecordGroup>,
    val selectedGroups: List<RecordGroup>,
    val selectMode : Boolean,
    val displayType : DisplayType,
) : Presenter.IUiState{
    enum class DisplayType(val code : Int){
        Normal(0),
        Incident(1),
        Locked(2)
    }
}