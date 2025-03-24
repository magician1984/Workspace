package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance

internal interface IRepo {
    val files : List<RecordFileInstance>

    fun add(file : RecordFileInstance) : Boolean
    fun remove(id : Int) : Boolean
    fun get(id : Int) : RecordFileInstance?
    fun update(id: Int, newFile : RecordFileInstance) : Boolean
    fun pop(file : RecordFileInstance) : RecordFileInstance?
    fun filter(predicate : (RecordFileInstance)->Boolean) : List<RecordFileInstance>
}