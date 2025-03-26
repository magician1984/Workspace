package com.auo.dvr.filemanager.operator.task

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.operator.IOperatorTask

internal class DeleteTask(recordFile: RecordFileInstance) : IOperatorTask(recordFile, Method.Delete) {
    override fun process() {
        recordFile.info.file?.delete()
    }

    override fun postProcess() {

    }

    override fun combine(task: IOperatorTask): Boolean = false

}