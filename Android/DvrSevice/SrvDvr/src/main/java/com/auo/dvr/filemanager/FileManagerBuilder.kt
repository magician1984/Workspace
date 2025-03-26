package com.auo.dvr.filemanager

import android.os.Environment
import com.auo.dvr.DvrService
import java.io.File

class FileManagerBuilder : DvrService.IFileManager.Builder<FileManager> {

    private var targetRoot: File = Environment.getDataDirectory()

    fun setTargetRoot(targetRoot: File) = this.apply { this.targetRoot = targetRoot }

    override fun build(): FileManager {
        // Check and Create target root

        TODO()
    }
}