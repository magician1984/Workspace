package com.auo.dvr.recordmanager

import com.auo.dvr_core.RecordGroup
import java.io.File

internal val RecordGroup.name : String
    get() = this.timestamp.toString()

internal fun RecordGroup.moveToExternal(root : File) : RecordGroup{
    val targetFolder = File(root, this.name)
    TODO()
}

internal fun RecordGroup.moveTo(root : File) : RecordGroup{
    TODO()
}

internal fun RecordGroup.delete() : RecordGroup{
    TODO()
}
