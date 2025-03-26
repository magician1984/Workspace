package com.auo.dvr.launcher

import android.os.Environment
import android.os.FileObserver
import com.auo.dvr.DvrConfigure
import com.auo.dvr.DvrService
import java.io.File

internal class DvrLauncher(sharedPartitionFolder: File) : DvrService.IDvrLauncher {
    companion object {
        private const val EVENT_FILE_EXTENSION = ".evt"
        private const val RECORD_FILE_EXTENSION = ".h265"
    }

    override val configureFile: DvrConfigure
        get() = DvrConfigure(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Dvr_dst"
            )
        )

    override var onRecordFileUpdateListener: DvrService.IDvrLauncher.OnRecordFileUpdateListener? =
        null

    private val mFileObserver: FileObserver =
        object : FileObserver(sharedPartitionFolder.absolutePath, CLOSE_WRITE or CREATE) {
            override fun onEvent(event: Int, path: String?) {
                if (path == null || !(path.endsWith(EVENT_FILE_EXTENSION) || path.endsWith(
                        RECORD_FILE_EXTENSION
                    ))
                ) return

                val fileType: DvrService.IDvrLauncher.FileType =
                    if (path.endsWith(EVENT_FILE_EXTENSION)) DvrService.IDvrLauncher.FileType.Event else DvrService.IDvrLauncher.FileType.Record

                if (event == CREATE)
                    onRecordFileUpdateListener?.onFileCreated(
                        fileType,
                        File(sharedPartitionFolder, path)
                    )
                else if (event == CLOSE_WRITE)
                    onRecordFileUpdateListener?.onFileClosed(
                        fileType,
                        File(sharedPartitionFolder, path)
                    )
            }
        }


    override fun start() {
        mFileObserver.startWatching()
    }

    override fun stop() {
        mFileObserver.stopWatching()
    }


}