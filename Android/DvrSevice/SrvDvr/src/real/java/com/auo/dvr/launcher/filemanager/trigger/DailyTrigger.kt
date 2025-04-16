package com.auo.dvr.launcher.filemanager.trigger

import android.icu.util.Calendar
import com.auo.dvr.launcher.filemanager.FileManager
import com.auo.dvr.data.RecordFileBundle
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

internal class DailyTrigger(private val keepDays: Int) : FileManager.ITriggerNotifier {
    override var callback: ((predicate: (RecordFileBundle) -> Boolean) -> Unit)? = null

    private val timerThread : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        timerThread.scheduleWithFixedDelay({
            callback?.invoke(this::predicate)
        }, 0, 1, TimeUnit.DAYS)
    }

    protected fun finalize(){
        timerThread.shutdown()
    }

    private fun predicate(bundle: RecordFileBundle): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = bundle.createTime
        calendar.add(Calendar.DAY_OF_YEAR, keepDays)
        return calendar.timeInMillis >= System.currentTimeMillis()
    }
}