package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.FileType

internal interface IFilePool {
    val recordFiles : List<RecordFileInstance>
    fun addFile(recordFile: RecordFileInstance) : Boolean

    fun updateFile(hash: Int, newRecordFile: RecordFileInstance) : Boolean
    fun removeFile(hash: Int) : RecordFileInstance?
    fun getRecordFile(hash : Int) : RecordFileInstance?
    fun getRecordsByLocation(camLocationDef: CamLocation) : List<RecordFileInstance>
    fun getRecordsByType(recordFileType: FileType) : List<RecordFileInstance>
    fun clear()
}