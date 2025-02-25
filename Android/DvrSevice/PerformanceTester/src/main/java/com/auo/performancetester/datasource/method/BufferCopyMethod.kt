package com.auo.performancetester.datasource.method

import com.auo.performancetester.domain.datasource.IDataSource
import java.io.File

class BufferCopyMethod :IDataSource.ICloneMethod{
    private companion object{
        const val BUFFER_SIZE : Int = 1024
    }
    override fun clone(source: File, target: File) {
        source.copyTo(target, true, BUFFER_SIZE)
    }
}