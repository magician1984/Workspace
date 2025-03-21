package com.auo.dvr.manager.filemanager

import java.io.File

interface IFileDetector {
    fun start()
    fun stop()
    fun getWritingFiles() : List<File>
    fun release()

    var onFileCreated : ((file : File)->Unit)?
    var onFileClosed : ((file : File)->Unit)?
}