package com.auo.dvr.launcher.observer

import com.auo.dvr.launcher.DvrLauncher
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import com.auo.dvr.launcher.DvrLauncher.IFileManager.EventType
import java.util.concurrent.Future

internal abstract class PollingFileObserver(
    private val mFolder: File,
    private val mInterval: Long
) : DvrLauncher.IFileObserver {

    private val mPollingThread: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: Future<*>? = null

    private val knownFiles = mutableMapOf<String, Long>() // name -> lastModified
    private val finalizedFiles = mutableSetOf<String>()   // already closed

    override fun start() {
        // ✅ 启动前先扫描一次目录，查找已存在且已完成的文件
        mFolder.listFiles()?.forEach { file ->
            knownFiles[file.name] = file.lastModified()
            if (file.canExecute()) {
                onEvent(EventType.Exist, file)
                finalizedFiles.add(file.name)
            }else{
                onEvent(EventType.Create, file)
            }
        }

        // ✅ 开始轮询
        mFuture = mPollingThread.scheduleWithFixedDelay(runnable, 0, mInterval, TimeUnit.MILLISECONDS)
    }

    override fun stop() {
        mFuture?.cancel(true)
        mPollingThread.shutdownNow()
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
}