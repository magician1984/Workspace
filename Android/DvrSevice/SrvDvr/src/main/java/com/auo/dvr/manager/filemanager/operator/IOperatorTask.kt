package com.auo.dvr.manager.filemanager.operator

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.FileInfo
import com.auo.dvr.manager.filemanager.exception.FileOperatorException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

internal abstract class IOperatorTask(val recordFile : RecordFileInstance, val method: Method) {
    enum class Method {
        Lock, Move, Delete
    }

    enum class Status(val code: Int) {
        Idle(0),
        Running(1),
        Done(2)
    }

    internal interface OnTaskCompleteListener {
        fun onTaskComplete(task: IOperatorTask)
    }

    val status: Status
        get() = Status.entries.find { it.code == _status.get() } ?: Status.Idle

    var onTaskCompleteListener: OnTaskCompleteListener? = null

    private val _status: AtomicInteger = AtomicInteger(Status.Idle.code)

    private var next: IOperatorTask? = null

    private var mLock: ReentrantLock? = null

    private var mProcessFuncPtr: (() -> Unit) = ::process

    private var mPostProcessFuncPtr: (() -> Unit) = ::postProcess

    init {
        if (recordFile.info !is FileInfo)
            throw FileOperatorException("Init", "recordFile.info must be FileInfo")
    }

    fun summit(lock: ReentrantLock) {
        mLock = lock

        runWithLock {
            _status.compareAndSet(Status.Idle.code, Status.Running.code)
            mProcessFuncPtr()
            _status.compareAndSet(Status.Running.code, Status.Done.code)
        }

        next?.summit(lock) ?: run {
            mPostProcessFuncPtr()
            onTaskCompleteListener?.onTaskComplete(this)
        }
    }

    fun link(task: IOperatorTask): Boolean {
        if(task.recordFile.id != recordFile.id)
            return false

        if (next == null) {
            runWithLock {
                if (_status.get() != Status.Idle.code)
                    return false

                if (!combine(task))
                    next = task

                return true
            }
        } else {
            return next!!.link(task)
        }
    }

    internal abstract fun process()

    internal abstract fun postProcess()

    protected abstract fun combine(task: IOperatorTask): Boolean

    protected fun updateProcessFuncPtr(funcPtr: () -> Unit) {
        mProcessFuncPtr = funcPtr
    }

    protected fun updatePostProcessFuncPtr(funcPtr: () -> Unit) {
        mPostProcessFuncPtr = funcPtr
    }

    private inline fun <R> runWithLock(block: () -> R): R {
        mLock?.lock()

        return try {
            block()
        } finally {
            mLock?.unlock()
        }
    }
}