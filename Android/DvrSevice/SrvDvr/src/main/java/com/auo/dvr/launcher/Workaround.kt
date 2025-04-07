package com.auo.dvr.launcher

import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import java.io.File
import java.util.concurrent.Executors

class Workaround {
    companion object{
        private const val WORKAROUND_FILE_EXTENSION = "h265"
        private const val RECORD_FILE_EXTENSION = "mp4"
    }

    private val executor = Executors.newFixedThreadPool(1)



    fun process(file: File){
        executor.execute(WorkaroundTask(file))
    }

    private class WorkaroundTask(val file: File) : Runnable{
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

            val command = listOf(
                "-re",
                "-f", "hevc",
                "-i", file.absolutePath,
                "-c", "copy",
                outputFile.absolutePath
            ).joinToString(" ") { "\"$it\"" } // wrap in quotes for safety

            Log.d("Workaround", "FFmpeg command: $command")

            val ret = FFmpegKit.execute(command)

            Log.d("Workaround", "process: ret = ${ret.returnCode}")

            file.delete()

            Thread.sleep(500)
        }
    }
}