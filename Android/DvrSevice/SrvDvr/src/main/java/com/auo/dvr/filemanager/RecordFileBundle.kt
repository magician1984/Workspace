package com.auo.dvr.filemanager

import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordType
import com.auo.dvr_core.RecordFile
import java.io.File

internal data class RecordFileBundle(val recordFile: RecordFile, var info: Info) {
    companion object{
        fun toRecordFileList(recordFileBundles: List<RecordFileBundle>) : List<RecordFile> = recordFileBundles.map { it.recordFile }
        fun toRecordFile(recordFileBundle: RecordFileBundle) : RecordFile = recordFileBundle.recordFile
    }

    interface Info{
        val file : File?

        companion object{
            fun empty() : Info = object : Info {
                override val file: File? = null
            }
        }
    }

    val id : Int = hashCode()

    val name : String = recordFile.name

    val location : CamLocation = recordFile.location

    val type : RecordType = recordFile.type

    val createTime : Long = recordFile.createTime

    val file : File?
        get() = info.file

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true

        if(other is RecordFileBundle)
            return hashCode() == other.hashCode()

        if(other is RecordFile)
            return hashCode() == other.hashCode()

        return false
    }

    override fun hashCode(): Int {
        return recordFile.hashCode()
    }

    override fun toString(): String {
        return "RecordFileInstance(id='$id', name='$name', location=$location, fileType=$type, createTime=$createTime, file=$file)"
    }
}