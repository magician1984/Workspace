package com.auo.performancetester.datasource.method

import com.auo.performancetester.domain.datasource.IDataSource
import java.io.File

class BufferCopyMethod :IDataSource.ICloneMethod{
    private companion object{
        const val BUFFER_SIZE : Int = 1024
    }
    override fun clone(source: List<File>, target: List<File>) {
        for (i in source.indices) {
            source[i].copyTo(target[i], true, BUFFER_SIZE)
        }
    }
}