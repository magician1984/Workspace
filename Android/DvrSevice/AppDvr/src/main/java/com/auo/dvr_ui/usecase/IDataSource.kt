package com.auo.dvr_ui.usecase

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.entity.DvrStateData

interface IDataSource {
    fun interface EventListener{
        fun onUpdate(records : List<RecordGroup>)
    }

    fun interface DvrStateListener{
        fun onUpdate(state : DvrStateData)
    }

    val dvrState : DvrStateData

    fun getAllRecords() : List<RecordGroup>
    fun registerUpdateListener(listener: EventListener)
    fun registerDvrStateListener(listener: DvrStateListener)
    fun lockRecord(record: RecordGroup)
    fun unlockRecord(record: RecordGroup)
    fun deleteRecord(record: RecordGroup)
    fun unmountStorage()
    fun updateConfigure(configure: DvrConfigure)
    fun getConfigure() : DvrConfigure
}