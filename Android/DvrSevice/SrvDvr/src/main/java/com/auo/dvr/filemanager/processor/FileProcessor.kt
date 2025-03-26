package com.auo.dvr.filemanager.processor

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IFileProcessor
import com.auo.dvr.manager.filemanager.exception.FileProcessException
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.NonReadableChannelException

internal open class FileProcessor : IFileProcessor {
    override fun delete(recordFile: RecordFileInstance) {
        (recordFile.info as? RecordFileInstance.FileInfo)?.let {
            if(it.srcFile.exists())
                it.srcFile.delete()
            if(it.dstFile.exists())
                it.dstFile.delete()
        } ?: throw FileProcessException("Record file info is not FileInfo", "delete", "Record file info is not FileInfo")
    }

    override fun move(recordFile: RecordFileInstance, samePartition: Boolean): RecordFileInstance {
        return (recordFile.info as? RecordFileInstance.FileInfo)?.run {
            if(!this.isHolding)
                throw FileProcessException("Checking", "move", "Record file is not holding")

            if(!this.srcFile.exists())
                throw FileProcessException(this.srcFile, this.dstFile, "move", "Source file not exist")

            if(this.dstFile.exists())
                this.dstFile.delete()

            if(samePartition)
                moveFile(this.srcFile, this.dstFile)
            else {
                copyFile(this.srcFile, this.dstFile)
                this.srcFile.delete()
            }
            recordFile.copy(info = this.copy(isCloned = true))
        } ?: throw FileProcessException("Checking", "move", "Record file info is not FileInfo")
    }

    private fun moveFile(src: File, dst: File) : Unit = if(!src.renameTo(dst)) throw FileProcessException(src, dst, "move", "Rename failed") else Unit

    private fun copyFile(src: File, dst: File) {
        var srcChannel: FileChannel? = null
        var dstChannel: FileChannel? = null

        try {
            srcChannel = src.inputStream().channel
            dstChannel = dst.outputStream().channel

            dstChannel!!.transferFrom(srcChannel, 0, srcChannel!!.size())
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