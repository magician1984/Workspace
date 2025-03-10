package com.auo.dvr.manager.filemanager

import com.auo.dvr_core.CamLocationDef
import com.auo.dvr_core.RecordFileTypeDef
import com.auo.dvr_core.RecordFile
import java.io.File

internal class RecordFileInstance(name:String, locationDef: CamLocationDef, createTime: Long, typeDef: RecordFileTypeDef, isCloned: Boolean, var srcFile: File? = null, var destFile: File? = null) : RecordFile() {
    companion object{
        fun getHash(file : RecordFile): Int{
                var result = file.name.hashCode()
                result = 31 * result + file.location.hashCode()
                result = 31 * result + file.createTime.hashCode()
                result = 31 * result + file.type.hashCode()
                result = 31 * result + file.isCloned.hashCode()
                return result
        }
    }

    init {
        this.name = name
        this.location = locationDef.value
        this.createTime = createTime
        this.type = typeDef.value
        this.isCloned = isCloned
    }

    var camLocation : CamLocationDef
        get() = CamLocationDef.entries.find { it.value == location } ?: CamLocationDef.Unknown
        set(value) {
            location = value.value
        }

    var recordFileType : RecordFileTypeDef
        get() = RecordFileTypeDef.entries.find { it.value == type } ?: RecordFileTypeDef.Unknown
        set(value) {
            type = value.value
        }

    val file : File?
        get() = if(isCloned) destFile else srcFile

    constructor(recordFile: RecordFile): this(recordFile.name, CamLocationDef.entries.find { it.value == recordFile.location } ?: CamLocationDef.Unknown, recordFile.createTime, RecordFileTypeDef.entries.find { it.value == recordFile.type } ?: RecordFileTypeDef.Normal, recordFile.isCloned)

    override fun equals(other: Any?): Boolean {
        if(this === other)
            return true

        if(other is RecordFileInstance)
            return hashCode() == other.hashCode()

        if(other is RecordFile)
            return hashCode() == getHash(other)

        return false
    }

    override fun hashCode(): Int {
        return getHash(this)
    }

    override fun toString(): String {
        return "RecordFileInstance(srcFile=$srcFile, destFile=$destFile, camLocation=$camLocation, recordFileType=$recordFileType, file=$file)"
    }
}