package com.auo.performancetester.datasource.method

import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.dvr_core.DvrException
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.NonReadableChannelException

class FileChannelMethod : IDataSource.ICloneMethod {
    override fun clone(source: List<File>, target: List<File>) {
        for (i in source.indices) {
            try {
                source[i].inputStream().channel.use { srcChannel ->
                    target[i].outputStream().channel.use { dstChannel ->
                        dstChannel.transferFrom(srcChannel, 0, srcChannel.size())
                    }
                }
            } catch (e: IOException) {
                throw DvrException(this.javaClass.name, e.message ?: "Unknown IO Error")
            } catch (e: NonReadableChannelException) {
                throw DvrException(this.javaClass.name, e.message ?: "Non-readable channel error")
            }
        }
    }
}
