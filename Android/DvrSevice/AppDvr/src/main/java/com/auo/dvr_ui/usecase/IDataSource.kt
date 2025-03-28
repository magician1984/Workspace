package com.auo.dvr_ui.usecase

import com.auo.dvr_ui.entity.RecordFileData
import java.io.File

interface IDataSource {
    fun interface EventListener{
        fun onUpdate(records : List<RecordFileData>)
    }

    fun registerUpdateListener(listener: EventListener)
    fun lockRecord(record: RecordFileData)
    fun unlockRecord(record: RecordFileData)
    fun deleteRecord(record: RecordFileData)
    fun getCacheFile(record: RecordFileData) : File
}