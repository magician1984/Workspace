package com.auo.dvr.manager.filemanager.repo

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IRepo

internal abstract class CachedFileRepo : IRepo {
    internal data class CacheConfig(val maxCacheSize : Int)

    internal fun interface OnCacheFullListener{
        fun onCacheFull(file : List<RecordFileInstance>)
    }

    protected var mListener : OnCacheFullListener? = null

    fun setOnCacheFullListener(listener : OnCacheFullListener?){
        mListener = listener
    }

    companion object{
        fun enable(config : CacheConfig) : CachedFileRepo = object : CachedFileRepo(){
            private val mConfig : CacheConfig = config

            override val files: List<RecordFileInstance>
                get() = TODO("Not yet implemented")

            override fun add(file: RecordFileInstance): Boolean {
                TODO("Not yet implemented")
            }

            override fun remove(id: Int): Boolean {
                TODO("Not yet implemented")
            }

            override fun get(id: Int): RecordFileInstance? {
                TODO("Not yet implemented")
            }

            override fun update(id: Int, newFile: RecordFileInstance): Boolean {
                TODO("Not yet implemented")
            }

            override fun pop(file: RecordFileInstance): RecordFileInstance? {
                TODO("Not yet implemented")
            }

            override fun filter(predicate: (RecordFileInstance) -> Boolean): List<RecordFileInstance> {
                TODO("Not yet implemented")
            }
        }

        fun disable() : CachedFileRepo = object : CachedFileRepo(){
            override val files: List<RecordFileInstance>
                get() = emptyList()

            override fun add(file: RecordFileInstance): Boolean = false.also {
                mListener?.onCacheFull(listOf(file))
            }

            override fun remove(id: Int): Boolean = false

            override fun get(id: Int): RecordFileInstance? = null

            override fun update(id: Int, newFile: RecordFileInstance): Boolean = false

            override fun pop(file: RecordFileInstance): RecordFileInstance? = null

            override fun filter(predicate: (RecordFileInstance) -> Boolean): List<RecordFileInstance> = emptyList()

        }
    }
}