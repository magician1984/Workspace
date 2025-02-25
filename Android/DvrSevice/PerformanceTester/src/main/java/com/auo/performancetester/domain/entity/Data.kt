package com.auo.performancetester.domain.entity

import android.icu.text.DecimalFormat

enum class CloneMethod {
    BufferIO,
    FileChannel,
    DMA,
    Shell,
}


sealed class IData {
    data class TestResult(
        val fileSize: Long,
        val fileCount: Int,
        val cloneMethod: CloneMethod,
        val totalTime: Long
    ) : IData() {
        override fun toString(): String{
            val df = DecimalFormat("#,##0.00") // Format with commas and 2 decimal places
            val fileSizeMB = fileSize.toDouble() / 1024.0 / 1024.0
            val totalTimeMs = totalTime.toDouble() / 1_000_000.0 // Convert nanoseconds to milliseconds
            val avgTimeMs = totalTimeMs / fileCount

            return """
            File size: ${df.format(fileSizeMB)} MB
            File count: $fileCount
            Clone method: $cloneMethod
            Total time: ${df.format(totalTimeMs)} ms
            Average time per file: ${df.format(avgTimeMs)} ms
        """.trimIndent()
        }
    }

    data class EventMessage(val message: String) : IData() {
        override fun toString(): String = message
    }
}

