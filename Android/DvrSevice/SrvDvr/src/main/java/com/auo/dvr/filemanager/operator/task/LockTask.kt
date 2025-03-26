package com.auo.dvr.filemanager.operator.task

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.operator.IOperatorTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

internal class LockTask(recordFile: RecordFileInstance,private val timeout: Long) : IOperatorTask(recordFile, Method.Lock) {

    private val processLock : ReentrantLock = ReentrantLock()

    private val startSignal : Condition = processLock.newCondition()

    private val unlockSignal : Condition = processLock.newCondition()

    override fun process() {
        processLock.lock()

        startSignal.signal()

        unlockSignal.await(timeout, TimeUnit.MILLISECONDS)

        processLock.unlock()
    }

    override fun postProcess() {

    }

    override fun combine(task: IOperatorTask): Boolean {
        return false
    }

    fun waitForStart(){
        processLock.lock()

        startSignal.await()

        processLock.unlock()
    }

    fun unlock(){
        processLock.lock()

        unlockSignal.signal()

        processLock.unlock()
    }

}