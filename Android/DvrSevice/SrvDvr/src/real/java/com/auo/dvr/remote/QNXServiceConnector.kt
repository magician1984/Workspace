package com.auo.dvr.remote

import android.util.Log
import com.auo.dvr.IRemoteConnector
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class QNXServiceConnector(rootFolder : File) : IRemoteConnector {
    companion object{
        private const val TAG = "QNXServiceConnector"
        private const val LOCK_FILE_NAME = "configure.lock"
        private const val CHECK_INTERVAL = 5000L
    }

    private var _isAvailable = false
        set(value) {
            if(field != value){
                field = value
                listeners.forEach { it.onAvailable(value) }
            }
        }

    override val isAvailable: Boolean
        get()  = _isAvailable

    private val lockFile = File(rootFolder, LOCK_FILE_NAME)

    private val executorService : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private val runnable : Runnable = Runnable {
        _isAvailable = lockFile.exists()
        Log.d(TAG, "Path: ${lockFile.path}, isAvailable: $_isAvailable")
    }

    private var listeners : MutableList<IRemoteConnector.OnStateUpdateListener> = mutableListOf()

    init {
        executorService.scheduleWithFixedDelay(runnable, 0, CHECK_INTERVAL, java.util.concurrent.TimeUnit.MILLISECONDS)
    }

    override fun addOnStateUpdateListener(listener: IRemoteConnector.OnStateUpdateListener) {
        if(listeners.contains(listener))
            return
        listeners.add(listener)
    }

    override fun removeOnStateUpdateListener(listener: IRemoteConnector.OnStateUpdateListener) {
        if(!listeners.contains(listener))
            return
        listeners.remove(listener)
    }
}