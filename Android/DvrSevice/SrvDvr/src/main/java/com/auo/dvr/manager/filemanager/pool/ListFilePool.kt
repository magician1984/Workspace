package com.auo.dvr.manager.filemanager.pool

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IFilePool
import com.auo.dvr_core.CamLocationDef
import com.auo.dvr_core.RecordFileTypeDef

internal class ListFilePool : IFilePool {
    override val recordFiles: List<RecordFileInstance>
        get() = mRecordFiles

    private val mRecordFiles : MutableList<RecordFileInstance> = mutableListOf()

    override fun addFile(recordFile: RecordFileInstance) : Boolean  = if(mRecordFiles.contains(recordFile)) false else mRecordFiles.add(recordFile)

    override fun updateFile(hash: Int, newRecordFile: RecordFileInstance) : Boolean {
        val index = mRecordFiles.indexOfFirst { it.hashCode() == hash }
        if(index == -1)
            return false
        else{
            mRecordFiles[index] = newRecordFile
            return true
        }
    }

    override fun removeFile(hash: Int): RecordFileInstance?{
        val index = mRecordFiles.indexOfFirst { it.hashCode() == hash }
        if(index == -1)
            return null
        else{
            val result = mRecordFiles[index]
            mRecordFiles.removeAt(index)
            return result
        }
    }

    override fun getRecordFile(hash: Int): RecordFileInstance? = mRecordFiles.find { it.hashCode() == hash }

    override fun getRecordsByLocation(camLocationDef: CamLocationDef): List<RecordFileInstance> = mRecordFiles.filter { it.camLocation == camLocationDef }

    override fun getRecordsByType(recordFileType: RecordFileTypeDef): List<RecordFileInstance> = mRecordFiles.filter { it.recordFileType == recordFileType }

    override fun clear() = mRecordFiles.clear()
}