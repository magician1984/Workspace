package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance

internal interface IFileProcessor {
    // Delete file
    fun delete(recordFile: RecordFileInstance)

    // Move file between same storage
    fun move(recordFile: RecordFileInstance, samePartition : Boolean) : RecordFileInstance
}