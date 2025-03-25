package com.auo.dvr.manager.filemanager.repo

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IRepo
import java.io.File

internal class CachedFileRepo(private val config: CacheConfig, fileOperator: IRepo.IFileOperator, root: File) :
    IRepo(fileOperator, root) {
    internal data class CacheConfig(val maxCacheSize: Int, val enable: Boolean)

    private val _files: MutableList<RecordFileInstance> = mutableListOf()

    private var mRepo: IRepo? = null

    override val files: List<RecordFileInstance>
        get() = (_files + (mRepo?.files ?: mutableListOf())).sortedByDescending { it.createTime }

    override fun add(file: RecordFileInstance): Boolean = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepo?.add(file) ?: false })


    override fun remove(id: Int): Boolean = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepo?.remove(id) ?: false })

    override fun get(id: Int): RecordFileInstance? = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepo?.get(id) })


    override fun update(id: Int, newFile: RecordFileInstance): Boolean = checkEnable(onDisable = {
        TODO("Not yet implemented")
    }, onEnable = { return@checkEnable mRepo?.update(id, newFile) ?: false })

    override fun pop(file: RecordFileInstance): RecordFileInstance? = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = {return@checkEnable mRepo?.pop(file)})

    override fun filter(predicate: (RecordFileInstance) -> Boolean): List<RecordFileInstance> {
        return _files.filter(predicate) + (mRepo?.filter(predicate) ?: mutableListOf())
    }

    override fun lock(file: RecordFileInstance): Boolean = checkEnable(onEnable = {
        TODO("Not yet implemented")
    }, onDisable = { return@checkEnable mRepo?.lock(file) ?: false})

    override fun unlock(file: RecordFileInstance): Boolean = checkEnable(onEnable = {
        TODO()
    }, onDisable = { return@checkEnable mRepo?.unlock(file) ?: false})

    override fun link(repo: IRepo) {
        mRepo = repo
    }

    override fun clean() {
        _files.clear()
        mRepo?.clean()
    }

    private inline fun <R> checkEnable(onEnable: () -> R, onDisable: () -> R): R {
        return if (config.enable)
            onEnable()
        else
            onDisable()
    }
}