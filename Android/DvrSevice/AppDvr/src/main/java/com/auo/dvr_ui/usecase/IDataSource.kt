package com.auo.dvr_ui.usecase

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.entity.RecordFileData
import java.io.File

interface IDataSource {
    fun interface EventListener{
        fun onUpdate(records : List<RecordFileData>)
    }

    fun interface DvrStateListener{
        fun onUpdate(state : DvrStateData)
    }

    val dvrState : DvrStateData

    fun getAllRecords() : List<RecordFileData>
    fun registerUpdateListener(listener: EventListener)
    fun registerDvrStateListener(listener: DvrStateListener)
    fun lockRecord(record: RecordFileData)
    fun unlockRecord(record: RecordFileData)
    fun deleteRecord(record: RecordFileData)
    fun getCacheFile(record: RecordFileData) : File
    fun unmountStorage()
    fun updateConfigure(configure: DvrConfigure)
    fun getConfigure() : DvrConfigure
}