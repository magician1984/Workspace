package com.auo.performancetester.presentation.info

import com.auo.performancetester.domain.entity.IData
import com.auo.performancetester.presentation.IModel
import com.auo.performancetester.presentation.UiState
import com.auo.performancetester.presentation.UserIntent

sealed class InfoIntent : UserIntent{
    data object PagePrepare : InfoIntent()
}

data class InfoState(val messages:List<IData.EventMessage>, val results:List<IData.TestResult>) : UiState