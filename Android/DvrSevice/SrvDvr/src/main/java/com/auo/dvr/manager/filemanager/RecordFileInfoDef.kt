package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import java.io.File

data class CloneInfo(val isCloned: Boolean, val sourceFile: File, val destFile: File) : RecordFileInstance.Info{
    override val file: File = if(isCloned) destFile else sourceFile
}

data class EventInfo(val time : Long, val eventFlag : Int) : RecordFileInstance.Info{
    override val file: File? = null
}

data class FileInfo(val sourceFile: File) : RecordFileInstance.Info{
    override val file: File = sourceFile
}

data class CacheInfo(val sourceFile: File) : RecordFileInstance.Info{
    override val file: File = sourceFile
}