package com.auo.dvr_ui.usecase

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.entity.DvrStateData

interface IDataSource {
    fun interface RecordUpdateListener{
        fun onUpdate()
    }

    fun interface DveStateUpdateListener{
        fun onUpdate()
    }

    val dvrState : DvrStateData
    val recordGroups : List<RecordGroup>

    fun registerRecordUpdateListener(listener: RecordUpdateListener)
    fun registerDveStateUpdateListener(listener: DveStateUpdateListener)
    fun lockRecords(record: List<RecordGroup>)
    fun unlockRecords(record: List<RecordGroup>)
    fun deleteRecords(record: List<RecordGroup>)
    fun unmountStorage()
    fun getConfigure() : DvrConfigure
}