package com.auo.dvr.launcher.filemanager

import com.auo.dvr.data.RecordFileBundle
import java.io.File

internal data class EventInfo(val time : Long, val eventFlag : Int) : RecordFileBundle.Info{
    override val file: File? = null
}

internal data class FileInfo(override val file: File) : RecordFileBundle.Info
