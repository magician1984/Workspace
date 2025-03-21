package com.auo.performancetester.domain.datasource

import com.auo.performancetester.domain.entity.BlockStat
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.IData
import java.io.File

interface IDataSource {
    interface ICloneMethod{
        fun clone(source: List<File>, target: List<File>)
    }

    interface IPerformanceMonitor{
        fun start()
        fun stop()
        fun getResult() : BlockStat?
    }

    fun interface EventListener{
        fun onEvent(data : IData)
    }

    var eventListener : EventListener?

    fun initialize()

    fun startTest(method:CloneMethod, size : Long, count : Int)
}