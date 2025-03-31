package com.auo.dvr.launcher

import android.os.Environment
import android.os.FileObserver
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.auo.dvr.DvrConfigure
import com.auo.dvr.DvrService
import java.io.File

internal class DvrLauncher(private val sharedPartitionFolder: File) : DvrService.IDvrLauncher {
    companion object {
        private const val EVENT_FILE_EXTENSION = "evt"
        private const val WORKAROUND_FILE_EXTENSION = "h265"
        private const val RECORD_FILE_EXTENSION = "mp4"
    }

    override val configureFile: DvrConfigure
        get() = DvrConfigure(
            File(
                sharedPartitionFolder.parentFile,
                "Dvr_dst"
            )
        )

    private val workaround : Workaround = Workaround()

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
                    WORKAROUND_FILE_EXTENSION-> workaround(eventType, File(sharedPartitionFolder, path))
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
    private fun workaround(eventType: DvrService.IDvrLauncher.EventType,  file: File){
        if(eventType != DvrService.IDvrLauncher.EventType.Create)
            return
        workaround.process(file)
    }
}