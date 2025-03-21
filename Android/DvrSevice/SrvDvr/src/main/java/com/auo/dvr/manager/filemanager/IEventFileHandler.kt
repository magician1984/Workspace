package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance

internal interface IEventFileHandler {
    fun process(file: RecordFileInstance, currentFile: RecordFileInstance?, previousFile: RecordFileInstance?) : RecordFileInstance
}