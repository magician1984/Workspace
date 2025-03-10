package com.auo.dvr.manager.filemanager.standard

import com.auo.dvr.manager.filemanager.RecordFileInstance

internal interface IEventFileHandler {
    fun setSourceRecordFile(currentFile: RecordFileInstance?, previousFile: RecordFileInstance?)
    fun process(file: RecordFileInstance) : RecordFileInstance
}