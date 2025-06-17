package com.auo.dvr.recordmanager.cleaner

import com.auo.dvr.recordmanager.ICleaner
import com.auo.dvr.recordmanager.operator.RecordGroupOperator.delete
import com.auo.dvr_core.RecordGroup

/***
 * Clean old record everyday
 */
internal class DailyRecordCleaner(private val keepDayLimit : Int = 3) : ICleaner {
    private var mListener : ICleaner.OnTriggerListener? = null

    override fun process(recordGroups: MutableList<RecordGroup>) {
        // remove the timestamp earlier than keepDayLimit
        val threshold = System.currentTimeMillis() - keepDayLimit * 24 * 60 * 60 * 1000
        recordGroups.removeIf { group ->
            return@removeIf (group.timestamp < threshold).also {
                if(it)
                    group.delete()
            }
        }
    }

    override fun setOnTriggerListener(listener: ICleaner.OnTriggerListener) {
        mListener = listener
    }
}