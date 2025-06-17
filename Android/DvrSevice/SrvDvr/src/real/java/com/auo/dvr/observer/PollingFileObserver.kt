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

    private val knownFolders = mutableMapOf<String, Long>() // name -> lastModified
    private val finalizedFolders = mutableSetOf<String>()   // already closed

    private var mCallback : IFileObserver.OnEventCallback? = null

    override fun start() {
        // 先掃描現有已完成資料夾
        val existingFolders = mFolder.listFiles()?.filter { it.isDirectory } ?: emptyList()

        for (folder in existingFolders) {
            val folderName = folder.name
            if (finalizedFolders.contains(folderName)) continue

            val readyFile = File(folder, ".ready")
            if (readyFile.exists()) {
                onEvent(EventType.Close, folder)
                finalizedFolders.add(folderName)
            }
        }

        // 再啟動定時輪詢
        mFuture = mPollingThread.scheduleWithFixedDelay(runnable, 0, mInterval, TimeUnit.MILLISECONDS)
    }

    override fun stop() {
        mFuture?.cancel(true)
        mPollingThread.shutdownNow()
    }

    override fun setOnEventCallback(callback: IFileObserver.OnEventCallback) {
        mCallback = callback
    }

    private val runnable: Runnable = Runnable {
        val folders = mFolder.listFiles()?.filter { it.isDirectory } ?: return@Runnable

        for (folder in folders) {
            val folderName = folder.name
            val readyFile = File(folder, ".ready")

            // 第一次看到，先發 Create 事件
            if (!knownFolders.contains(folderName)) {
                onEvent(EventType.Create, folder)
                knownFolders[folderName] = folder.lastModified()
            }

            // 若未完成，檢查 .ready 是否出現 → Close
            if (!finalizedFolders.contains(folderName) && readyFile.exists()) {
                onEvent(EventType.Close, folder)
                finalizedFolders.add(folderName)
            }
        }
    }

    private fun onEvent(event: EventType, file: File) {
        mCallback?.onEvent(event, file)
    }
}