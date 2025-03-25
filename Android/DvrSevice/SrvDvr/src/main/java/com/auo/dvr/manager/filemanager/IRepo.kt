package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import java.io.File

internal abstract class IRepo(protected val fileOperator : IFileOperator, private val root : File) {

    internal interface IFileOperator{
        fun lock(file : RecordFileInstance, timeout : Long) : Boolean
        fun unlock(file : RecordFileInstance) : Boolean
        fun move(file : RecordFileInstance, dest : FileInfo, samePartition : Boolean) : Boolean
        fun delete(file : RecordFileInstance) : Boolean
    }

    abstract val files : List<RecordFileInstance>

    abstract fun add(file : RecordFileInstance) : Boolean
    abstract fun remove(id : Int) : Boolean
    abstract fun get(id : Int) : RecordFileInstance?
    abstract fun update(id: Int, newFile : RecordFileInstance) : Boolean
    abstract fun pop(file : RecordFileInstance) : RecordFileInstance?
    abstract fun filter(predicate : (RecordFileInstance)->Boolean) : List<RecordFileInstance>
    abstract fun lock(file : RecordFileInstance) : Boolean
    abstract fun unlock(file : RecordFileInstance) : Boolean
    abstract fun hold(file : RecordFileInstance) : Boolean
    abstract fun unhold(file : RecordFileInstance) : Boolean
    abstract fun link(repo : IRepo)
    abstract fun clean()
}