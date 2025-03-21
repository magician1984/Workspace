package com.auo.dvr.manager.filemanager.standard

import com.auo.dvr.manager.filemanager.RecordFileInstance

internal interface IFileProcessor {
    // Move file between different storage
    fun clone(recordFile: RecordFileInstance) : RecordFileInstance

    // Delete file
    fun delete(recordFile: RecordFileInstance)

    // Move file between same storage
    fun move(recordFile: RecordFileInstance) : RecordFileInstance
}