package com.auo.dvr_ui.framework.exo

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

class CustomTimer(
    private val durationMillis: Long,
    private val intervalMillis: Long,
    private val onTick: (elapsedMillis: Long, totalMillis: Long) -> Unit,
    private val onFinish: (() -> Unit)? = null,
    private val looper : Looper
) {
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L
    private var isRunning = false
    private var handler: Handler? = null
    private val runnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            val now = SystemClock.uptimeMillis()
            elapsedTime = now - startTime
            if (elapsedTime >= durationMillis) {
                isRunning = false
                handler?.removeCallbacks(this)
                onTick(durationMillis, durationMillis)
                onFinish?.invoke()
            } else {
                onTick(elapsedTime, durationMillis)
                handler?.postDelayed(this, intervalMillis)
            }
        }
    }

    fun start() {
        if (isRunning) return
        isRunning = true

        handler = Handler(looper)
        startTime = SystemClock.uptimeMillis() - elapsedTime
        handler?.post(runnable)
    }

    fun pause() {
        if (!isRunning) return
        isRunning = false
        handler?.removeCallbacks(runnable)
    }

    fun reset() {
        pause()
        elapsedTime = 0L
    }

    fun isRunning(): Boolean = isRunning
}