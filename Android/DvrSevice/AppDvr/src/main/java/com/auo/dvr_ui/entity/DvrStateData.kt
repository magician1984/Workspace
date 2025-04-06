package com.auo.dvr_ui.entity

import com.auo.dvr_core.DvrState

data class DvrStateData(val dto : DvrState, val errorMessage : String? = null){
    val isAvailable : Boolean = dto.isAvailable
    val errorType : DvrState.ErrorType = dto.errorType
}
