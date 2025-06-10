package com.auo.dvr

import com.auo.dvr_core.RecordGroup
import java.io.File

internal interface IRecordManager{
    fun interface OnRecordUpdateListener{
        fun onUpdate()
    }

    fun onRootFolderChanged(rootFolder : File?)
    fun onRecordGroupCreated(groupFolder : File)

    fun lockRecord(recordGroups: List<RecordGroup>)
    fun unlockRecord(recordGroups: List<RecordGroup>)
    fun deleteRecord(recordGroups: List<RecordGroup>)

    fun addRecordUpdateListener(listener: OnRecordUpdateListener)

    val recordGroups : List<RecordGroup>
}