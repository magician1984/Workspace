package com.auo.dvr.manager.filemanager

import android.os.Environment
import com.auo.dvr.manager.IFileManager
import java.io.File

class FileManagerBuilder : IFileManager.Builder<FileManager> {
    companion object{
        private const val MEDIA_FILE_EXTENSION_MP4 = ".mp4"
        private const val MEDIA_FILE_EXTENSION_HEVC = ".h265"

        private const val EVENT_FILE_EXTENSION = ".evt"
    }

    private var sourceRoot: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Dvr_src")

    private var targetRoot: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Dvr_dst")

    fun setSourceRoot(file : File) : FileManagerBuilder = this.apply { sourceRoot = file  }

    override fun build(): FileManager {
        // Check and Create target root
        if(!sourceRoot.exists()){
            if(!sourceRoot.mkdirs())
                throw com.auo.dvr.manager.filemanager.exception.FolderControlFailedException(
                    sourceRoot
                )
        }

        TODO()
    }
}