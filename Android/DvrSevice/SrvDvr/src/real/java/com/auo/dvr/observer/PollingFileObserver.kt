package com.auo.dvr.observer

import com.auo.dvr.IFileObserver
import com.auo.dvr.IFileObserver.EventType
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

internal class PollingFileObserver(
    private val mFolder: File,
    private val mInterval: Long
) : IFileObserver {

    private val mPollingThread: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: Future<*>? = null

    private val knownFiles = mutableMapOf<String, Long>() // name -> lastModified
    private val finalizedFiles = mutableSetOf<String>()   // already closed

    private var mCallback : IFileObserver.OnEventCallback? = null

    override fun start() {
        mFolder.listFiles()?.forEach { file ->
            knownFiles[file.name] = file.lastModified()
            if (file.canExecute()) {
                onEvent(EventType.Exist, file)
                finalizedFiles.add(file.name)
            }else{
                onEvent(EventType.Create, file)
            }
        }

        mFuture = mPollingThread.scheduleWithFixedDelay(runnable, 0, mInterval, TimeUnit.MILLISECONDS)
    }

    override fun stop() {
        mFuture?.cancel(true)
        mPollingThread.shutdownNow()
    }

    override fun setOnEventCallback(callback: IFileObserver.OnEventCallback) {
        TODO("Not yet implemented")
    }

    private val runnable: Runnable = Runnable {
        val currentFiles = mFolder.listFiles()?.associateBy({ it.name }, { it }) ?: return@Runnable
        val now = System.currentTimeMillis()

        // 1. Detect created or modified
        for ((name, file) in currentFiles) {
            val lastMod = file.lastModified()
            val prevMod = knownFiles[name]

            if (prevMod == null) {
                onEvent(EventType.Create, file)
            }

            knownFiles[name] = lastMod

            // 2. Check if file is now "ready" (has executable permission)
            if (file.canExecute() && !finalizedFiles.contains(name)) {
                onEvent(EventType.Close, file)
                finalizedFiles.add(name)
            }
        }

        // 3. Detect deleted
        val deletedNames = knownFiles.keys - currentFiles.keys
        for (name in deletedNames) {
            val deletedFile = File(mFolder, name)
            onEvent(EventType.Delete, deletedFile)
            knownFiles.remove(name)
            finalizedFiles.remove(name)
        }
    }

    private fun onEvent(event: EventType, file: File) {
        mCallback?.onEvent(event, file)
    }
}