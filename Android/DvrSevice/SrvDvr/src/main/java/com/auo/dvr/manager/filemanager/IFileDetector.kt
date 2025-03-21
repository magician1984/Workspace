package com.auo.dvr.manager.filemanager

import java.io.File

interface IFileDetector {
    var onFileCreated: ((file: File) -> Unit)?

    var onFileClosed: ((file: File) -> Unit)?

    fun start()

    fun stop()

    fun getWritingFiles(): List<File>

    fun release()
}
