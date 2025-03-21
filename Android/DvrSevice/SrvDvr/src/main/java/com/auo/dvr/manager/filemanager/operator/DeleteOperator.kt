package com.auo.dvr.manager.filemanager.operator

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IFileOperator
import com.auo.dvr.manager.filemanager.IFilePool

internal class DeleteOperator(recordFile: RecordFileInstance, pool: IFilePool) : IFileOperator(Method.Delete, recordFile, pool) {
    override fun execute(recordFile: RecordFileInstance, pool: IFilePool) {

    }
}