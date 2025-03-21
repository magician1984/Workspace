package com.auo.dvr.manager.filemanager.processor

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.exception.FileProcessException
import java.io.File

internal class MuxerFileProcessor : FileProcessor() {
    override fun move(recordFile: RecordFileInstance, samePartition: Boolean): RecordFileInstance {
        val newFile : RecordFileInstance = (recordFile.info as? RecordFileInstance.FileInfo)?.run{
            if(this.srcFile.extension == "h265")
                muxFile(recordFile)
            else{
                recordFile
            }
        } ?: throw FileProcessException("Record file info is not FileInfo", "move", "Record file info is not FileInfo")
        return super.move(newFile, samePartition)
    }

    private fun muxFile(recordFile: RecordFileInstance) : RecordFileInstance{
        TODO()
    }
}