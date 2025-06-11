package com.auo.dvr_ui.presentation

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.presentation.Presenter.Screen

data class GlobalState(
    val focusRecord: RecordGroup?,
    val filterType: Set<RecordType>,
    val records: List<RecordGroup>,
    val page: Screen
)
