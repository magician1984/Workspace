package com.auo.dvr.filemanager.repo

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.filemanager.IRepo
import java.io.File

internal class CachedFileRepo(private val config: Configure) :
    IRepo<CachedFileRepo.Configure>(config) {

    internal data class Configure(
        val maxCacheSize: Int, val enable: Boolean,
        override val fileOperator: IRepo.IFileOperator,
        override val root: File
    ) : IConfigure

    private val _files: MutableList<RecordFileInstance> = mutableListOf()

    override fun init() {

    }

    override val files: List<RecordFileInstance>
        get() = (_files + (mRepoLink?.files ?: mutableListOf())).sortedByDescending { it.createTime }

    override fun add(file: RecordFileInstance): Boolean = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepoLink?.add(file) ?: false })


    override fun remove(id: Int): Boolean = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepoLink?.remove(id) ?: false })

    override fun get(id: Int): RecordFileInstance? = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepoLink?.get(id) })


    override fun lock(file: RecordFileInstance): Boolean = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepoLink?.lock(file) ?: false })

    override fun unlock(file: RecordFileInstance): Boolean = checkEnable(onEnable = {
        TODO()
    }, onDisable = { return@checkEnable mRepoLink?.unlock(file) ?: false })

    override fun hold(file: RecordFileInstance): Boolean {
        TODO("Not yet implemented")
    }

    override fun unhold(file: RecordFileInstance): Boolean {
        TODO("Not yet implemented")
    }

    override fun clean() {
        _files.clear()
        mRepoLink?.clean()
    }

    private inline fun <R> checkEnable(onEnable: () -> R, onDisable: () -> R): R {
        return if (config.enable)
            onEnable()
        else
            onDisable()
    }
}