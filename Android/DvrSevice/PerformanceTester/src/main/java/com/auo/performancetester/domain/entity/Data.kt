package com.auo.performancetester.domain.entity

import android.icu.text.DecimalFormat

enum class CloneMethod {
    BufferIO,
    FileChannel
}

enum class FileAllocateMode {
    NonPreAllocate,
    PreAllocate
}

enum class FileSize {
    Small,
    Medium,
    Large
}

enum class ThreadSize{
    One,
    Two,
    Three,
    Four
}


data class TestCaseConfigure(
    val cloneMethod: CloneMethod,
    val allocateMode: FileAllocateMode,
    val fileSize: FileSize,
    val fileCount: Int,
    val forceWrite: Boolean = true,
    val bufferSize: Int = -1,
    val threadSize: ThreadSize = ThreadSize.One
) {
    override fun toString(): String {
        val bufferSizeStr =
            if (cloneMethod == CloneMethod.BufferIO && bufferSize != -1) "_$bufferSize" else ""
        val syncMode = if (forceWrite) "_sync" else "_async"
        val threadSizeStr = "_${threadSize.name}"
        return "${cloneMethod}_${allocateMode}_${fileSize}_$fileCount$syncMode$bufferSizeStr$threadSizeStr"
    }
}


sealed class IData {
    data class TestResult(
        val fileSize: Long,
        val fileCount: Int,
        val cloneMethod: CloneMethod,
        val totalTime: Long,
        val performanceData: BlockStat? = null
    ) : IData() {
        override fun toString(): String {
            val df = DecimalFormat("#,##0.00") // Format with commas and 2 decimal places
            val fileSizeMB = fileSize.toDouble() / 1024.0 / 1024.0
            val totalTimeMs =
                totalTime.toDouble() / 1_000_000.0 // Convert nanoseconds to milliseconds
            val avgTimeMs = totalTimeMs / fileCount

            return """
            File size: ${df.format(fileSizeMB)} MB
            File count: $fileCount
            Clone method: $cloneMethod
            Total time: ${df.format(totalTimeMs)} ms
            Average time per file: ${df.format(avgTimeMs)} ms
            ${performanceData?.toString() ?: ""}
        """.trimIndent()
        }
    }

    data class EventMessage(val message: String) : IData() {
        override fun toString(): String = message
    }
}

