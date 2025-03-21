package com.auo.dvr.manager.filemanager.standard

import com.auo.dvr.manager.filemanager.RecordFileInstance

internal interface IEventFileHandler {
    fun process(file: RecordFileInstance, currentFile: RecordFileInstance?, previousFile: RecordFileInstance?) : RecordFileInstance
}