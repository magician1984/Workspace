package com.auo.dvr.manager.filemanager.operator.task

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.operator.FileOperator
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

internal class LockTask(recordFile: RecordFileInstance,private val timeout: Long) : FileOperator.IOperatorTask(recordFile.id) {
    private val mLocker : Lock = ReentrantLock()
    private val mCondition = mLocker.newCondition()

    override fun process() {
        mLocker.lock()
        try{
            mCondition.await(timeout, TimeUnit.MILLISECONDS)
        }finally {
            mLocker.unlock()
        }
    }

    fun unlock(){
        mLocker.lock()
        try{
            mCondition.signal()
        }finally {
            mLocker.unlock()
        }
    }
}