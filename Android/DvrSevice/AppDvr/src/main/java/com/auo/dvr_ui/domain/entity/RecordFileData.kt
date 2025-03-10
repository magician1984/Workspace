package com.auo.dvr_ui.domain.entity

import com.auo.dvr_core.CamLocationDef
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordFileTypeDef

data class RecordFileData constructor(private val recordFile: RecordFile) {
    val name : String = recordFile.name
    val location : CamLocationDef = CamLocationDef.entries.find { it.value == recordFile.location } ?: CamLocationDef.Unknown
    val createTime : Long = recordFile.createTime
    val type : RecordFileTypeDef = RecordFileTypeDef.entries.find { it.value == recordFile.type } ?: RecordFileTypeDef.Normal
    val isCloned : Boolean = recordFile.isCloned
}