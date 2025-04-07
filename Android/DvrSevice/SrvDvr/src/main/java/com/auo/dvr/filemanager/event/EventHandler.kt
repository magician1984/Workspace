package com.auo.dvr.filemanager.event

import com.arthenica.ffmpegkit.FFmpegKit
import com.auo.dvr.filemanager.EventInfo
import com.auo.dvr.filemanager.FileManager
import com.auo.dvr.filemanager.RecordFileBundle
import com.auo.dvr_core.CamLocation
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean

internal class EventHandler(private val mTmpFolder: File) : FileManager.IEventHandler {
    override var onComplete: ((File) -> Unit)? = null

    private val mExecutorService = Executors.newFixedThreadPool(CamLocation.entries.size)

    override fun handleEvent(
        event: RecordFileBundle,
        writingFile: RecordFileBundle,
        previousFileBundle: RecordFileBundle
    ) {
        mExecutorService.submit(EventHandleTask(event, writingFile, previousFileBundle))
    }

    private inner class EventHandleTask(
        val event: RecordFileBundle,
        val writingFile: RecordFileBundle,
        val previousFileBundle: RecordFileBundle
    ) : Runnable {
        override fun run() {
            val eventTime = (event.info as? EventInfo)?.time ?: throw IllegalArgumentException("EventInfo is null")

            val startTime = eventTime - 30 * 1000

            val previousFileTime = previousFileBundle.createTime
            val writingFileTime = writingFile.createTime



            val file = File(mTmpFolder, event.name.substringBefore(".") + ".mp4")

//            val ret = if(startTime > writingFileTime){
//                val startTimeInSec = (startTime - writingFileTime) / 1000
//                FFmpegKit.execute("-i ${writingFile.file!!.absolutePath} -ss $startTimeInSec -t 90 ${file.absolutePath}")
//            }else{
//                val startTimeInSec = (writingFileTime - startTime - previousFileTime) / 1000
//
//            }

            onComplete?.invoke(file)
        }
    }
}