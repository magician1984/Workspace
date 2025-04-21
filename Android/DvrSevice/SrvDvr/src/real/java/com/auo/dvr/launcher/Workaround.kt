package com.auo.dvr.launcher

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.auo.dvr.launcher.DvrLauncher.IFileManager.EventType
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

internal class Workaround {
    companion object{
        private const val WORKAROUND_FILE_EXTENSION = "h265"
        private const val RECORD_FILE_EXTENSION = "mp4"
        private const val EVENT_FILE_EXTENSION = "evt"
    }

    var mOut : DvrLauncher.IFileManager? = null

    private val nonClosedFile = mutableListOf<File>()

    private val executor = Executors.newFixedThreadPool(1)

    fun process(file: File, immediate : Boolean = false){
        executor.execute(WorkaroundTask(file, immediate))
    }

    fun onEvent(event: EventType, file: File){
        if(file.extension == WORKAROUND_FILE_EXTENSION){
            when (event) {
                EventType.Exist -> {
                    process(file, true)
                }
                EventType.Create -> {
                    process(file, false)
                    nonClosedFile.add(file)
                }
                EventType.Close -> {
                    nonClosedFile.remove(file)
                }
                else->{}
            }
        }else if(file.extension == EVENT_FILE_EXTENSION){
            mOut?.onFileUpdate(eventType = event, type = DvrLauncher.IFileManager.FileType.Event, file = file)
        }
    }

    private inner class WorkaroundTask(val file: File, val immediate : Boolean = false) : Runnable{
        private var retryCount : Int = 0

        override fun run() {
            Log.d("Workaround", "process: file = ${file.absolutePath}")

            if (!file.exists()) {
                Log.e("Workaround", "process: file not exists")
                return
            }

            val outputFile = File(
                file.parent,
                file.name.replace(WORKAROUND_FILE_EXTENSION, RECORD_FILE_EXTENSION)
            )

            if(immediate){
                Thread.sleep(100)
            }

            val command = listOfNotNull(
                if (!immediate) "-re" else null,
                "-f", "hevc",
                "-i", file.absolutePath,
                "-c:v", "copy",
                "-movflags", "+frag_keyframe+empty_moov+default_base_moof+faststart",
                outputFile.absolutePath
            ).joinToString(" ") { "\"$it\"" }

            Log.d("Workaround", "FFmpeg command: $command")

            mOut?.onFileUpdate(eventType = EventType.Create, type = DvrLauncher.IFileManager.FileType.Record, file = outputFile)

            val ret = FFmpegKit.execute(command)

            if(outputFile.exists()){
                outputFile.setReadable(true, false)
                outputFile.setWritable(true, false)
            }

            Log.d("Workaround", "process: ret = ${ret.returnCode}")

            if(ret.returnCode.isValueSuccess){
                mOut?.onFileUpdate(eventType = EventType.Close, type = DvrLauncher.IFileManager.FileType.Record, file = outputFile)
                file.delete()
            }else{
                if(outputFile.exists()){
                    outputFile.delete()
                }
                if(retryCount < 3){
                    Log.e("Workaround", "process: retryCount = $retryCount")
                    retryCount++
                    executor.execute(this)
                }else{
                    Log.e("Workaround", "process: retryCount > 3")
                }
            }

            Thread.sleep(100)
        }
    }
}