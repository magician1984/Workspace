package com.auo.dvr.manager.filemanager.standard.processor

import com.auo.dvr.manager.filemanager.standard.StandardFileManager
import com.auo.dvr.manager.filemanager.RecordFileInstance
import com.auo.dvr.manager.filemanager.standard.IFileProcessor

internal open class StandardFileProcessor : IFileProcessor {
//    override fun process(src: File, dst: File) {
//        var srcChannel: FileChannel? = null
//        var dstChannel: FileChannel? = null
//
//        try {
//            srcChannel = src.inputStream().channel
//            dstChannel = dst.outputStream().channel
//
//            dstChannel.transferFrom(srcChannel, 0, srcChannel.size())
//        } catch (e: IOException) {
//            throw DvrException(this.javaClass.name, e.message.toString())
//        } catch (e: NonReadableChannelException) {
//            throw DvrException(this.javaClass.name, e.message.toString())
//        } finally {
//            srcChannel?.close()
//            dstChannel?.close()
//        }
//    }

    override fun clone(recordFile: RecordFileInstance): RecordFileInstance {
        TODO("Not yet implemented")
    }

    override fun delete(recordFile: RecordFileInstance) {
        TODO("Not yet implemented")
    }

    override fun move(recordFile: RecordFileInstance): RecordFileInstance {
        TODO("Not yet implemented")
    }
}