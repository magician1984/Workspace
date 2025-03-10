package com.auo.dvr.manager.filemanager.standard.processor

import com.auo.dvr.manager.filemanager.FileManagerException
import com.auo.dvr.manager.filemanager.standard.StandardFileManager
import com.auo.dvr.manager.filemanager.RecordFileInstance
import com.auo.dvr.manager.filemanager.standard.IFileProcessor
import com.auo.dvr.manager.filemanager.standard.exception.FileProcessException
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.NonReadableChannelException

internal open class StandardFileProcessor : IFileProcessor {


    override fun clone(recordFile: RecordFileInstance): RecordFileInstance {
        (recordFile.info as RecordFileInstance.FileInfo).let {
            copyFile(it.srcFile, it.dstFile)
            recordFile.isCloned = true
        }
        return recordFile
    }

    override fun delete(recordFile: RecordFileInstance) {
        TODO("Not yet implemented")
    }

    override fun move(recordFile: RecordFileInstance): RecordFileInstance {
        TODO("Not yet implemented")
    }

    private fun copyFile(src: File, dst: File) {
        var srcChannel: FileChannel? = null
        var dstChannel: FileChannel? = null

        try {
            srcChannel = src.inputStream().channel
            dstChannel = dst.outputStream().channel

            dstChannel.transferFrom(srcChannel, 0, srcChannel.size())
        } catch (e: IOException) {
            throw FileProcessException(src, dst, "copy", e.message.toString())
        } catch (e: NonReadableChannelException) {
            throw FileProcessException(src, dst, "copy", e.message.toString())
        } finally {
            srcChannel?.close()
            dstChannel?.close()
        }
    }
}