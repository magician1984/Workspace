package com.auo.dvr.filemanager.repo

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.filemanager.IRepo
import com.auo.dvr_core.CamLocation
import java.io.File

internal class ExternalFileRepo(configure : Configure) : IRepo<ExternalFileRepo.Configure>(configure) {

    companion object{
        private const val LOCK_FOLDER = "Lock"
    }

    internal data class Configure(
        override val fileOperator: IRepo.IFileOperator,
        override val root: File
    ) : IConfigure

    private val _files : MutableList<RecordFileInstance> = mutableListOf()

    private val folderMap : HashMap<CamLocation, File> = hashMapOf()

    override val files: List<RecordFileInstance>
        get() = _files

    override fun init() {
        getRoot().apply {
            if(!exists())
                mkdirs()

            CamLocation.entries.forEach {
                folderMap[it] = File(this, it.name).apply {
                    // Create camera folders
                    if (!exists())
                        mkdirs()

                    // Create lock folders
                    File(this, LOCK_FOLDER).apply {
                        if (!exists())
                            mkdirs()
                    }
                }
            }
        }
    }

    override fun add(file: RecordFileInstance): Boolean {
        TODO()
    }

    override fun remove(id: Int): Boolean {
        return _files.removeIf { it.id == id }
    }

    override fun get(id: Int): RecordFileInstance? {
        return _files.find { it.id == id }
    }

    override fun lock(file: RecordFileInstance): Boolean {
        TODO("Not yet implemented")
    }

    override fun unlock(file: RecordFileInstance): Boolean {
        TODO("Not yet implemented")
    }

    override fun hold(file: RecordFileInstance): Boolean {
        TODO("Not yet implemented")
    }

    override fun unhold(file: RecordFileInstance): Boolean {
        TODO("Not yet implemented")
    }

    override fun link(repo: IRepo<*>) {
        TODO("Not yet implemented")
    }

    override fun clean() {
        TODO("Not yet implemented")
    }
}