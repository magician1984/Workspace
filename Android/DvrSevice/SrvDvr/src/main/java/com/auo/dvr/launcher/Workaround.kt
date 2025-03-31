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

    private val executor = Executors.newFixedThreadPool(2)

    private val runningMap : MutableList<File> = mutableListOf()

    fun process(file: File){
        if(runningMap.contains(file))
            return

        Log.d("Workaround", "process: file = ${file.absolutePath}")

        runningMap.add(file)

        executor.execute {
            val ret = FFmpegKit.execute(
                "-re -i ${file.absolutePath} -c:v copy -c:a copy ${
                    file.absolutePath.replace(
                        WORKAROUND_FILE_EXTENSION,
                        RECORD_FILE_EXTENSION
                    )
                }"
            )
            runningMap.remove(file)

            Log.d("Workaround", "process: ret = ${ret.returnCode}")
        }


    }
}