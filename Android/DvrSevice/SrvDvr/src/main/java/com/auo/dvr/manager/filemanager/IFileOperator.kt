package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance

internal interface IFileOperator {
    fun lock(recordFile: RecordFileInstance, timeout: Long): Boolean
    fun unlock(recordFile: RecordFileInstance): Boolean
    fun move(recordFile: RecordFileInstance, dest: FileInfo, samePartition: Boolean): Boolean
    fun delete(recordFile: RecordFileInstance): Boolean
}