package com.auo.dvr.recordmanager

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import java.io.File

internal interface IConvertor {
    fun parse(folder: File, overrideType : RecordType? = null): RecordGroup
}