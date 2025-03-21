package com.auo.dvr.manager.filemanager.eventhandler

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.standard.IEventFileHandler

internal class StandardEventFileHandler : IEventFileHandler {
    override fun process(
        file: RecordFileInstance,
        currentFile: RecordFileInstance?,
        previousFile: RecordFileInstance?
    ): RecordFileInstance {
        TODO("Not yet implemented")
    }

}