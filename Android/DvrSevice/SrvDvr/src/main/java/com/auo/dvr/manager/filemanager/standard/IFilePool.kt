package com.auo.dvr.manager.filemanager.standard

import com.auo.dvr.manager.filemanager.RecordFileInstance
import com.auo.dvr_core.CamLocationDef
import com.auo.dvr_core.RecordFileTypeDef

internal interface IFilePool {
    val recordFiles : List<RecordFileInstance>
    fun addFile(recordFile: RecordFileInstance) : Boolean
    fun updateFile(hash: Int, newRecordFile: RecordFileInstance) : Boolean
    fun removeFile(hash: Int) : RecordFileInstance?
    fun getRecordFile(hash : Int) : RecordFileInstance?
    fun getRecordsByLocation(camLocationDef: CamLocationDef) : List<RecordFileInstance>
    fun getRecordsByType(recordFileType: RecordFileTypeDef) : List<RecordFileInstance>
    fun clear()
}