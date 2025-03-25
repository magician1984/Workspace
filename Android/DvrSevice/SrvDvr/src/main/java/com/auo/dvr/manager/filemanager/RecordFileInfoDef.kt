package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import java.io.File

data class EventInfo(val time : Long, val eventFlag : Int) : RecordFileInstance.Info{
    override val file: File? = null
}

internal interface FileInfo : RecordFileInstance.Info{
    override val file: File
}

data class ExternalFileInfo(val sourceFile: File) : FileInfo{
    override val file: File = sourceFile
}

data class CacheInfo(val sourceFile: File) : FileInfo{
    override val file: File = sourceFile
}

data class EventFileInfo(val sourceFile:File, val flag: Int) : FileInfo{
    override val file: File = sourceFile
}