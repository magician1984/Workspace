package com.auo.dvr

import com.auo.dvr_core.IDvrService

internal abstract class IDvrServiceApi : IDvrService.Stub() {
    abstract fun setRecordManager(recordManager: IRecordManager?)
}