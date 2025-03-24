package com.auo.dvr.manager.filemanager.operator

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.FileInfo
import com.auo.dvr.manager.filemanager.IFileOperator
import com.auo.dvr.manager.filemanager.operator.task.LockTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

internal class FileOperator : IFileOperator {
    companion object {
        private const val MAX_OPERATOR_THREAD = 4
    }

    internal abstract class IOperatorTask(val id: Int) : Runnable {
        private var isDone: AtomicBoolean = AtomicBoolean(false)

        private var linked: IOperatorTask? = null

        override fun run() {

            process()

            isDone.set(true)
            linked?.run()
        }

        protected abstract fun process()

        fun link(task: IOperatorTask): Boolean {
            return if (linked == null) {
                if (isDone.get())
                    false
                else {
                    linked = task
                    true
                }
            } else {
                linked!!.link(task)
            }
        }
    }

    private val executorService: ExecutorService = Executors.newFixedThreadPool(MAX_OPERATOR_THREAD)

    private val taskMap: HashMap<Int, IOperatorTask> = HashMap()

    private var currentLockFile: LockTask? = null

    override fun lock(recordFile: RecordFileInstance, timeout: Long): Boolean {
        if (currentLockFile != null)
            return false

        currentLockFile = LockTask(recordFile, timeout).also {
            push(it)
        }

        return true
    }

    override fun unlock(recordFile: RecordFileInstance): Boolean {
        if (currentLockFile?.id != recordFile.id)
            return false

        currentLockFile?.unlock()

        return true
    }

    override fun move(
        recordFile: RecordFileInstance,
        dest: FileInfo,
        samePartition: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(recordFile: RecordFileInstance): Boolean {
        TODO("Not yet implemented")
    }

    private fun push(task : IOperatorTask){
        val taskRoot : IOperatorTask? = taskMap[task.id]

        if(taskRoot?.link(task) != true) {
            taskMap[task.id] = task
            executorService.submit(task)
        }
    }
}