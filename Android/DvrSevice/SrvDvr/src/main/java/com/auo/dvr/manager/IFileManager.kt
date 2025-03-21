package com.auo.dvr.manager

import com.auo.dvr.manager.filemanager.FileManagerConfig
import com.auo.dvr_core.FileType
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

    fun holdFile(recordFile: RecordFile)
    fun releaseFile(recordFile: RecordFile)
    fun getFilePath(recordFile: RecordFile) : String
    fun deleteFile(recordFile: RecordFile)
    fun changType(recordFile: RecordFile, type: FileType)
    fun lockFile(recordFile: RecordFile)
    fun unlockFile(recordFile: RecordFile)
    fun forceClone()
}