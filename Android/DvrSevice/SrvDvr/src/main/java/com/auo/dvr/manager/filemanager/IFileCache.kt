package com.auo.dvr.manager.filemanager

import java.io.File

internal interface IFileCache {
    fun cache(file: File)

    fun setCallback(callback : (files: List<File>) -> Unit)

    fun forcePush()
}