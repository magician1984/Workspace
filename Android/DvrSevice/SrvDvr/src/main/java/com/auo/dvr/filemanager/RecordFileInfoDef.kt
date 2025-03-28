package com.auo.dvr.filemanager

import java.io.File

internal data class EventInfo(val time : Long, val eventFlag : Int) : RecordFileBundle.Info{
    override val file: File? = null
}

internal data class FileInfo(override val file: File) : RecordFileBundle.Info
