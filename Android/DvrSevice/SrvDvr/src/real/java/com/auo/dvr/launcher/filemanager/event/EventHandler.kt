package com.auo.dvr.launcher.filemanager.event

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.auo.dvr.launcher.filemanager.EventInfo
import com.auo.dvr.launcher.filemanager.FileManager
import com.auo.dvr.data.RecordFileBundle
import com.auo.dvr_core.CamLocation
import java.io.File
import java.util.concurrent.Executors

internal class EventHandler(private val mTmpFolder: File) : FileManager.IEventHandler {
    companion object{
        private const val DURATION_BEFORE_EVENT_SEC = 30
        private const val DURATION_AFTER_EVENT_SEC = 60
    }

    override var onComplete: ((File) -> Unit)? = null

    private val mExecutorService = Executors.newFixedThreadPool(CamLocation.entries.size)

    override fun handleEvent(
        event: RecordFileBundle,
        writingFile: RecordFileBundle,
        previousFileBundle: RecordFileBundle?
    ) {
        mExecutorService.submit(EventHandleTask(event, writingFile, previousFileBundle))
    }

    private inner class EventHandleTask(
        val event: RecordFileBundle,
        val writingFile: RecordFileBundle,
        val previousFileBundle: RecordFileBundle?
    ) : Runnable {
        override fun run() {
            Log.d("EventHandler", "handleEvent: ${event.name}")
            val eventTime = (event.info as? EventInfo)?.time
                ?: throw IllegalArgumentException("EventInfo is null")

            Log.d("EventHandler", "handleEvent: $eventTime")

            val startTime = eventTime - DURATION_BEFORE_EVENT_SEC * 1000
            val duration = DURATION_BEFORE_EVENT_SEC + DURATION_AFTER_EVENT_SEC
            val endTime = eventTime + DURATION_AFTER_EVENT_SEC * 1000

            val writingFileTime = writingFile.createTime
            val filename = event.name.substringBefore(".")
            val file = File(mTmpFolder, "$filename.mp4")

            // 💤 Wait until writing file has full data
            val now = System.currentTimeMillis()
            val waitTime = endTime - now
            if (waitTime > 0) {
                Log.d("EventHandler", "Waiting $waitTime ms for writing file to complete")
                Thread.sleep(waitTime)
            }

            val ret: FFmpegSession

            if (startTime >= writingFileTime) {
                Log.d("EventHandler", "handleEvent: Case 1, startTime >= writingFileTime")
                val startTimeInSec = (startTime - writingFileTime) / 1000
                val command =
                    "-y -ss $startTimeInSec -i ${writingFile.file!!.absolutePath} -t $duration -c copy ${file.absolutePath}"
                ret = FFmpegKit.execute(command)
            } else {
                Log.d("EventHandler", "handleEvent: Case 2, startTime < writingFileTime")

                val tempPrev = File(mTmpFolder, "${filename}_prev.mp4")
                val tempCurr = File(mTmpFolder, "${filename}_curr.mp4")
                val listFile = File(mTmpFolder, "${filename}_list.txt")
                var fileList = ""
                var subRet: FFmpegSession
                var durationInPrev = 0L

                previousFileBundle?.let {
                    val previousFileTime = it.createTime
                    val startInPrev = (startTime - previousFileTime) / 1000
                    durationInPrev =
                        ((writingFileTime - startTime).coerceAtMost((duration * 1000).toLong())) / 1000
                    val cmdPrev =
                        "-y -ss $startInPrev -i ${it.file!!.absolutePath} -t $durationInPrev -c copy ${tempPrev.absolutePath}"

                    Log.d("EventHandler", "Handle prev. Execute: $cmdPrev")
                    subRet = FFmpegKit.execute(cmdPrev)
                    Log.d("EventHandler", "subRet (prev): ${subRet.returnCode}")
                    if (subRet.returnCode.isValueSuccess) {
                        fileList += "file '${tempPrev.absolutePath}'\n"
                    }

                    Thread.sleep(500)
                }

                val durationInCurr =
                    DURATION_BEFORE_EVENT_SEC + DURATION_AFTER_EVENT_SEC - durationInPrev / 1000

                val cmdCurr =
                    "-y -ss 0 -i ${writingFile.file!!.absolutePath} -t $durationInCurr -c copy ${tempCurr.absolutePath}"

                Log.d("EventHandler", "Handle curr. Execute: $cmdCurr")
                subRet = FFmpegKit.execute(cmdCurr)
                Log.d("EventHandler", "subRet (curr): ${subRet.returnCode}")
                if (subRet.returnCode.isValueSuccess) {
                    fileList += "file '${tempCurr.absolutePath}'\n"
                }

                listFile.writeText(fileList)
                val concatCmd =
                    "-y -f concat -safe 0 -i ${listFile.absolutePath} -c copy ${file.absolutePath}"
                Log.d("EventHandler", "Handle concat. Execute: $concatCmd")
                subRet = FFmpegKit.execute(concatCmd)
                Log.d("EventHandler", "subRet (concat): ${subRet.returnCode}")
                ret = subRet

                // Clean up temp files
                tempPrev.delete()
                tempCurr.delete()
                listFile.delete()
            }

            Log.d("EventHandler", "handleEvent result: ${ret.returnCode}")
            if (ret.returnCode.isValueSuccess) {
                onComplete?.invoke(file)
            }
        }
    }
}