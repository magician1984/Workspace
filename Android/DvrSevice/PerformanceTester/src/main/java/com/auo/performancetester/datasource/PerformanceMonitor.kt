package com.auo.performancetester.datasource

import android.util.Log
import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.BlockStat
import java.io.File

object PerformanceMonitor {
    private const val TAG = "PerformanceMonitor"
    fun disable(log: Boolean = false) : IDataSource.IPerformanceMonitor = object : IDataSource.IPerformanceMonitor {
        override fun start() {
            Log.d(TAG, "start: Disable")
        }

        override fun stop() {
            Log.d(TAG, "stop: Disable")
        }

        override fun getResult(): BlockStat? = null
    }

    fun enable() : IDataSource.IPerformanceMonitor = object : IDataSource.IPerformanceMonitor {
        private var startState: BlockStat? = null
        private var endState: BlockStat? = null

        override fun start() {
            startState = getBlockStat()
        }

        override fun stop() {
            endState = getBlockStat()
        }

        override fun getResult(): BlockStat = endState!! - startState!!

        private fun getBlockStat(): BlockStat {
            val file = File("/sys/block/sda/stat")
            val line = file.readLines()
            return line.firstOrNull()?.let { BlockStat.fromStatLine(it) } ?: BlockStat.empty()
        }
    }
}