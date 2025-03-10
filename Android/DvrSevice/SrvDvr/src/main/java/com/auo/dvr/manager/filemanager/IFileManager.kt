package com.auo.dvr.manager.filemanager

import com.auo.dvr_core.RecordFileTypeDef
import com.auo.dvr_core.RecordFile

interface IFileManager {
    interface Builder<T : IFileManager>{
        fun build() : T
    }

    fun interface RecordUpdateListener{
        fun onUpdate()
    }

    var recordUpdateListener: RecordUpdateListener?

    val recordFiles : List<RecordFile>

    fun init(config: FileManagerConfig)
    fun start()
    fun stop()
    fun release()

    fun getFilePath(recordFile: RecordFile) : String
    fun deleteFile(recordFile: RecordFile)
    fun changType(recordFile: RecordFile, type: RecordFileTypeDef)
    fun lockFile(recordFile: RecordFile)
    fun unlockFile(recordFile: RecordFile)
}