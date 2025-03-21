package com.auo.dvr.manager.filemanager.operator

import android.database.Observable
import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.IFileOperator
import com.auo.dvr.manager.filemanager.IFilePool
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

internal class HoldOperator(recordFile: RecordFileInstance, pool: IFilePool, private val timeout : Long) : IFileOperator(Method.Hold, recordFile, pool) {

    private val mLock : ReentrantLock = ReentrantLock()

    private val condition : Condition = mLock.newCondition()

    override fun execute(recordFile: RecordFileInstance, pool: IFilePool) {
        mLock.lock()
        try{
            condition.await(timeout, TimeUnit.MILLISECONDS)
        }finally{
            mLock.unlock()
        }
    }

    fun release(){
        mLock.lock()
        try{
            condition.signal()
        }finally{
            mLock.unlock()
        }
    }
}