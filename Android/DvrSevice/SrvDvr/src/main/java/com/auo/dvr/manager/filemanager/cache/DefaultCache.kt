package com.auo.dvr.manager.filemanager.cache
//0
import com.auo.dvr.manager.filemanager.standard.IFileCache
import java.io.File

internal class DefaultCache : IFileCache {
    private val cacheFiles : MutableList<File> = mutableListOf()

    override fun cache(file: File) {

    }

    override fun setCallback(callback: (files: List<File>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun forcePush() {
        TODO("Not yet implemented")
    }

}