package com.auo.dvr.filemanager.operator

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.FileInfo
import com.auo.dvr.manager.filemanager.IRepo
import com.auo.dvr.manager.filemanager.operator.task.DeleteTask
import com.auo.dvr.manager.filemanager.operator.task.LockTask
import com.auo.dvr.manager.filemanager.operator.task.MoveTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

internal class FileOperator : IRepo.IFileOperator {
    companion object {
        private const val MAX_OPERATOR_THREAD = 4
    }

    private val executorService: ExecutorService = Executors.newFixedThreadPool(MAX_OPERATOR_THREAD, object : ThreadFactory{
        private val index : AtomicInteger = AtomicInteger(0)

        override fun newThread(r: Runnable?): Thread = Thread(r, "FileOperator-${index.getAndIncrement()}")
    })

    private val lockerList : HashMap<String, ReentrantLock> = HashMap()

    private val taskMap: HashMap<Int, IOperatorTask> = HashMap()

    private var currentLockFile: LockTask? = null

    override fun lock(file: RecordFileInstance, timeout: Long): Boolean {
        if (currentLockFile != null)
            return false

        currentLockFile = LockTask(file, timeout)

        pushOrLink(currentLockFile!!)

        currentLockFile?.waitForStart()

        return true
    }


    override fun unlock(file: RecordFileInstance): Boolean {
        if (currentLockFile?.recordFile!!.id != file.id)
            return false

        currentLockFile?.unlock()

        return true
    }

    override fun move(
        file: RecordFileInstance,
        dest: FileInfo,
        samePartition: Boolean
    ): Boolean {
        pushOrLink(MoveTask(file, dest, samePartition))
        return true
    }

    override fun delete(file: RecordFileInstance): Boolean {
        pushOrLink(DeleteTask(file))
        return true
    }

    private fun pushOrLink(task : IOperatorTask){
        val taskRoot : IOperatorTask? = taskMap[task.recordFile.id]

        if(taskRoot?.link(task) != true) {
            taskMap[task.recordFile.id] = task
            executorService.submit{
                val key = Thread.currentThread().name
                val lock = getLock(key)
                task.summit(lock)
            }
        }
    }

    private fun getLock(key : String) : ReentrantLock{
        var locker = lockerList[key]

        if(locker == null) {
            locker = ReentrantLock()
            lockerList[key] = locker
        }
        return locker
    }
}