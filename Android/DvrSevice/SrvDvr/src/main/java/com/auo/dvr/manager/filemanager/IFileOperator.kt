package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance

internal abstract class IFileOperator(val method: Method, val recordFile: RecordFileInstance, private val pool : IFilePool) : Runnable{
    internal enum class Method{
        Move,
        Delete,
        Hold
    }

    private var linkedOperator : IFileOperator? = null

    override fun run() {
        internalRun(recordFile)
    }

    fun link(operator: IFileOperator) : Unit = linkedOperator?.link(operator) ?: run { linkedOperator = operator }

    private fun internalRun(newRecordFile : RecordFileInstance){
        val renewFile = execute(newRecordFile, pool)
        linkedOperator?.let { next ->
            val combineFile =next.combineInput(renewFile)
            next.internalRun(combineFile)
        }?: pool.updateFile(renewFile.hashCode(), renewFile)
    }

    //Renew the record file for next operator before the next execute
    protected abstract fun combineInput(renewFile : RecordFileInstance) : RecordFileInstance

    //Main implementation of the operator
    protected abstract fun execute(recordFile: RecordFileInstance, pool: IFilePool) : RecordFileInstance
}