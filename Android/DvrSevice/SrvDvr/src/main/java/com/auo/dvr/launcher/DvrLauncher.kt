package com.auo.dvr.launcher

import android.os.Environment
import android.os.FileObserver
import android.util.Log
import com.auo.dvr.DvrConfigure
import com.auo.dvr.DvrService
import java.io.File

internal class DvrLauncher(sharedPartitionFolder: File) : DvrService.IDvrLauncher {
    companion object {
        private const val EVENT_FILE_EXTENSION = "evt"
        private const val WORKAROUND_FILE_EXTENSION = "h265"
        private const val RECORD_FILE_EXTENSION = "mp4"
    }

    override val configureFile: DvrConfigure
        get() = DvrConfigure(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Dvr_dst"
            )
        )

    init {
        Log.d("DvrLauncher", "sharedPartitionFolder: ${sharedPartitionFolder.absolutePath}, target folder: ${configureFile.destinationFolder.absolutePath}")
    }

    override var onRecordFileUpdateListener: DvrService.IDvrLauncher.OnRecordFileUpdateListener? =
        null

    private val mFileObserver: FileObserver =
        object : FileObserver(sharedPartitionFolder.absolutePath, CLOSE_WRITE or CREATE) {
            override fun onEvent(event: Int, path: String?) {
                if(path == null)
                    return

                val eventType : DvrService.IDvrLauncher.EventType = if(event == CLOSE_WRITE) DvrService.IDvrLauncher.EventType.Close else DvrService.IDvrLauncher.EventType.Create
                Log.d("DvrLauncher", "onEvent: $eventType, $path")
                when (path.substringAfterLast('.')){
                    EVENT_FILE_EXTENSION-> onRecordFileUpdateListener?.onFileUpdate(eventType, DvrService.IDvrLauncher.FileType.Event, File(sharedPartitionFolder, path))
                    RECORD_FILE_EXTENSION-> onRecordFileUpdateListener?.onFileUpdate(eventType, DvrService.IDvrLauncher.FileType.Record, File(sharedPartitionFolder, path))
                    WORKAROUND_FILE_EXTENSION-> workaround(File(sharedPartitionFolder, path))
                }
            }
        }


    override fun start() {
        mFileObserver.startWatching()
    }

    override fun stop() {
        mFileObserver.stopWatching()
    }

    // Workaround: Provider can not generate mp4 file, so we need to convert h265 to mp4
    private fun workaround(file: File){
//        val ffmpeg = FFmpeg()
//        ffmpeg.execute("-i ${file.absolutePath} -c:v copy -c:a copy ${file.absolutePath.replace(WORKAROUND_FILE_EXTENSION, RECORD_FILE_EXTENSION)}")
        val targetFile = File(file.parentFile, file.name.replace(WORKAROUND_FILE_EXTENSION, RECORD_FILE_EXTENSION))
        file.copyTo(targetFile)
        file.delete()
    }
}