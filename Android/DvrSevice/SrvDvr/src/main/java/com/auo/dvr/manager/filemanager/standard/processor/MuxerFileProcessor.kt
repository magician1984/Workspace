package com.auo.dvr.manager.filemanager.standard.processor

import com.auo.dvr.manager.filemanager.RecordFileInstance
import java.io.File

internal class MuxerFileProcessor : StandardFileProcessor() {

    override fun clone(recordFile: RecordFileInstance): RecordFileInstance {
        TODO()
    }

    override fun delete(recordFile: RecordFileInstance) {
        TODO()
    }

    override fun move(recordFile: RecordFileInstance): RecordFileInstance {
        TODO()
    }

    private fun preProcess(file : File) : File{
        TODO("Transform encoded raw file into mp4 file")
    }
}