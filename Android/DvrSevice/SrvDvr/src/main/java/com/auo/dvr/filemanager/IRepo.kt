package com.auo.dvr.filemanager

import com.auo.dvr.RecordFileInstance
import java.io.File

internal abstract class IRepo<T : IRepo.IConfigure>(protected val configure : T) {

    internal interface IFileOperator{
        fun lock(file : RecordFileInstance, timeout : Long) : Boolean
        fun unlock(file : RecordFileInstance) : Boolean
        fun move(file : RecordFileInstance, dest : FileInfo, samePartition : Boolean) : Boolean
        fun delete(file : RecordFileInstance) : Boolean
    }

    abstract val files : List<RecordFileInstance>

    protected var mRepoLink : IRepo<*>? = null

    internal interface IConfigure{
        val fileOperator : IFileOperator
        val root : File
    }

    open fun init(){
        mRepoLink?.init()
    }

    open fun link(repo : IRepo<*>){
        mRepoLink = repo
    }

    abstract fun clean()

    abstract fun add(file : RecordFileInstance) : Boolean
    abstract fun remove(id : Int) : Boolean
    abstract fun get(id : Int) : RecordFileInstance?
    abstract fun lock(file : RecordFileInstance) : Boolean
    abstract fun unlock(file : RecordFileInstance) : Boolean
    abstract fun hold(file : RecordFileInstance) : Boolean
    abstract fun unhold(file : RecordFileInstance) : Boolean

    protected inline fun<R> withOperator(block : IFileOperator.() -> R) : R = block(configure.fileOperator)

    protected fun getRoot() : File = configure.root

    protected fun pushTask(){

    }
}