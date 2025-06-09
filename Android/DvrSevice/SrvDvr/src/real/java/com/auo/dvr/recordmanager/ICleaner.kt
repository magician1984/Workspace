package com.auo.dvr.recordmanager

import com.auo.dvr_core.RecordGroup

interface ICleaner {
    fun interface OnTriggerListener{
        fun onTrigger()
    }

    fun process(recordGroups: MutableList<RecordGroup>)

    fun setOnTriggerListener(listener: OnTriggerListener)
}