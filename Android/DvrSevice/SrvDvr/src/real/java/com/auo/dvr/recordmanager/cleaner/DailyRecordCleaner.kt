package com.auo.dvr.recordmanager.cleaner

import com.auo.dvr.recordmanager.ICleaner
import com.auo.dvr_core.RecordGroup

/***
 * Clean old record everyday
 */
internal class DailyRecordCleaner : ICleaner {
    private var mListener : ICleaner.OnTriggerListener? = null

    override fun process(recordGroups: MutableList<RecordGroup>) {

    }

    override fun setOnTriggerListener(listener: ICleaner.OnTriggerListener) {
        mListener = listener
    }
}