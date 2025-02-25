package com.auo.performancetester.datasource.method

import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.DvrException
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.NonReadableChannelException

class FileChannelMethod : IDataSource.ICloneMethod {
    override fun clone(source: File, target: File) {
        var srcChannel: FileChannel? = null
        var dstChannel: FileChannel? = null

        try {
            srcChannel = source.inputStream().channel
            dstChannel = target.outputStream().channel

            dstChannel.transferFrom(srcChannel, 0, srcChannel.size())
        } catch (e: IOException) {
            throw DvrException(this.javaClass.name, e.message.toString())
        } catch (e: NonReadableChannelException) {
            throw DvrException(this.javaClass.name, e.message.toString())
        } finally {
            srcChannel?.close()
            dstChannel?.close()
        }
    }
}
