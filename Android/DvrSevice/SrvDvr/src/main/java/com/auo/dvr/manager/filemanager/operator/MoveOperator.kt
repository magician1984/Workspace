package com.auo.dvr.manager.filemanager.operator

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IFileOperator
import com.auo.dvr.manager.filemanager.IFilePool

internal class MoveOperator(recordFile: RecordFileInstance, pool: IFilePool, samePartition: Boolean) : IFileOperator(Method.Move, recordFile, pool) {
    override fun execute(recordFile: RecordFileInstance, pool: IFilePool) {
        TODO("Not yet implemented")
    }
}