package com.auo.dvr_ui.usecase

import android.os.IBinder
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.entity.DvrStateData

interface IDataSource {
    interface EventListener{
        fun onRecordUpdate()
        fun onStateUpdate()
    }

    val dvrState : DvrStateData
    val recordGroups : List<RecordGroup>
    
    fun registerUpdateListener(listener: EventListener)
    fun lockRecords(record: List<RecordGroup>)
    fun unlockRecords(record: List<RecordGroup>)
    fun deleteRecords(record: List<RecordGroup>)
    fun unmountStorage()
    fun getConfigure() : DvrConfigure
}