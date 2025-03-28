package com.auo.dvr.filemanager.event

import com.auo.dvr.filemanager.FileManager
import com.auo.dvr.filemanager.RecordFileBundle

internal class EventHandler : FileManager.IEventHandler {
    override fun handleEvent(
        event: RecordFileBundle,
        records: List<RecordFileBundle>
    ): RecordFileBundle {
        TODO("Not yet implemented")
    }

}