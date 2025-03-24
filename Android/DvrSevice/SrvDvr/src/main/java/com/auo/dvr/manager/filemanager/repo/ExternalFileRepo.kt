package com.auo.dvr.manager.filemanager.repo

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IRepo

internal class ExternalFileRepo : IRepo {
    private val _files : MutableList<RecordFileInstance> = mutableListOf()

    override val files: List<RecordFileInstance>
        get() = _files

    override fun add(file: RecordFileInstance): Boolean {
        return _files.add(file)
    }

    override fun remove(id: Int): Boolean {
        return _files.removeIf { it.id == id }
    }

    override fun get(id: Int): RecordFileInstance? {
        return _files.find { it.id == id }
    }

    override fun update(id: Int, newFile: RecordFileInstance): Boolean {
        val index = _files.indexOfFirst { it.id == id }

        if(index == -1)
            return false

        _files[index] = newFile
        return true
    }

    override fun pop(file: RecordFileInstance): RecordFileInstance? {
        val index = _files.indexOf(file)

        if(index == -1)
            return null

        return _files.removeAt(index)
    }

    override fun filter(predicate: (RecordFileInstance) -> Boolean): List<RecordFileInstance> = _files.filter(predicate)
}