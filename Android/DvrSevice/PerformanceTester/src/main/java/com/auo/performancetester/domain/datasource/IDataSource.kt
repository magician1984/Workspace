package com.auo.performancetester.domain.datasource

import com.auo.performancetester.domain.entity.BlockStat
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.IData
import com.auo.performancetester.domain.entity.TestCaseConfigure
import java.io.File

interface IDataSource {
    interface ICloneMethod{
        companion object{
            const val DEFAULT_BUFFER_SIZE = 4 * 1024
        }
        fun clone(source: File, target: File, preallocate: Boolean, forceWrite : Boolean)
    }

    interface IPerformanceMonitor{
        fun start()
        fun stop()
        fun getResult() : BlockStat?
    }

    interface IResultWriter{
        fun open(configure : TestCaseConfigure)
        fun write(index : Int, stat : BlockStat)
        fun close()
    }

    fun interface EventListener{
        fun onEvent(data : IData)
    }

    var eventListener : EventListener?

    fun initialize()

    fun startTest(configure : TestCaseConfigure)
}