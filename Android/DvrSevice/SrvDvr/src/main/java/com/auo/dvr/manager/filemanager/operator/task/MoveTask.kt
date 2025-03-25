package com.auo.dvr.manager.filemanager.operator.task

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.FileInfo
import com.auo.dvr.manager.filemanager.operator.IOperatorTask

internal class MoveTask(
    recordFile: RecordFileInstance,
    private var dest: FileInfo,
    private var samePartition: Boolean
) : IOperatorTask(recordFile, Method.Move) {

    override fun process() {
        val src: FileInfo = recordFile.info as FileInfo

        if (samePartition)
            src.file.renameTo(dest.file)
        else {
            src.file.copyTo(dest.file, true)
            src.file.delete()
        }
    }

    override fun postProcess() {
        recordFile.info = dest
    }

    override fun combine(task: IOperatorTask): Boolean {
        return when (task.method) {
            Method.Lock -> false
            Method.Move -> {
                val nextTask = task as MoveTask
                dest = nextTask.dest
                samePartition = nextTask.samePartition
                true
            }
            Method.Delete -> {
                updateProcessFuncPtr {
                    task.recordFile.info = recordFile.info
                    task.process()
                }
                updatePostProcessFuncPtr { task.postProcess() }
                true
            }
        }
    }

}