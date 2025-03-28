package com.auo.dvr_ui.domain.entity

import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordType

data class RecordFileData(val dto: RecordFile) {
    val id : Int = dto.hashCode()
    val name : String = dto.name
    val location : CamLocation = dto.location
    val type : RecordType = dto.type
    val createTime : Long = dto.createTime
}