package com.auo.dvr.manager.filemanager.cache

import com.auo.dvr.manager.filemanager.standard.IFileCache
import java.io.File

internal class NonCache : IFileCache {
    private var mCallback : ((files: List<File>) -> Unit)? = null

    override fun cache(file: File) {
        mCallback?.invoke(listOf(file))
    }

    override fun setCallback(callback: (files: List<File>) -> Unit) {
        mCallback = callback
    }

    override fun forcePush() {
        // Nothing to do.
    }

}