package com.auo.dvr_ui.presentation.contents.list

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.presentation.Presenter

sealed class UserIntent : Presenter.IUserIntent{
    data object Init : UserIntent()
    data class ItemClicked(val item: RecordGroup) : UserIntent()
    data object Delete : UserIntent()
    data object Lock : UserIntent()
    data object Unlock : UserIntent()
    data class DisplayTypeChanged(val type: UiState.DisplayType) : UserIntent()
    data class SelectModeChanged(val selectMode: Boolean) : UserIntent()
    data object SelectAll : UserIntent()
}