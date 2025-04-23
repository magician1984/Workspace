package com.auo.dvr_ui.entity

import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordType
import java.io.File

data class RecordFileData(val dto: RecordFile, val cacheFile : File? = null) {
    val id : Int = dto.hashCode()
    val name : String = dto.name
    val location : CamLocation = dto.location
    val type : RecordType = dto.type
    val createTime : Long = dto.createTime

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordFileData

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }


}