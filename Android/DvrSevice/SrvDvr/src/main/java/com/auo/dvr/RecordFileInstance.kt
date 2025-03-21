package com.auo.dvr

import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.FileType
import com.auo.dvr_core.RecordFile
import java.io.File
import java.nio.file.Files

internal data class RecordFileInstance(private val recordFile: RecordFile, val info: Info) {
    companion object{
        fun toRecordFileList(recordFileInstances: List<RecordFileInstance>) : List<RecordFile> = recordFileInstances.map { it.recordFile }
        fun toRecordFile(recordFileInstance: RecordFileInstance) : RecordFile = recordFileInstance.recordFile
    }

    interface Info{
        val file : File?
    }

    val id : Int = hashCode()

    val name : String = recordFile.name

    val location : CamLocation = recordFile.location

    val fileType : FileType = recordFile.type

    val createTime : Long = recordFile.createTime

    val file : File?
        get() = info.file

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true

        if(other is RecordFileInstance)
            return hashCode() == other.hashCode()

        if(other is RecordFile)
            return hashCode() == other.hashCode()

        return false
    }

    override fun hashCode(): Int {
        return recordFile.hashCode()
    }

    override fun toString(): String {
        return "RecordFileInstance(id='$id', name='$name', location=$location, fileType=$fileType, createTime=$createTime, file=$file)"
    }
}