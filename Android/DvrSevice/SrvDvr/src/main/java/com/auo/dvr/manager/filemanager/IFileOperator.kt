package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import java.util.concurrent.Callable

internal abstract class IFileOperator(val method: Method, val recordFile: RecordFileInstance) : Callable<RecordFileInstance>{
    internal enum class Method{
        Move,
        Convert,
        Mux,
        Delete,
        Hold
    }

    private var linkedOperator : IFileOperator? = null

    override fun call(): RecordFileInstance {
       return internalRun(recordFile)
    }

    fun link(operator: IFileOperator) : Unit = linkedOperator?.link(operator) ?: run { linkedOperator = operator }

    private fun internalRun(newRecordFile : RecordFileInstance): RecordFileInstance{
        var retFile = execute(newRecordFile)

        linkedOperator?.let {
            retFile = it.internalRun(combineInput(retFile))
        }

        return retFile
    }

    //Renew the record file for next operator before the next execute
    protected abstract fun combineInput(renewFile : RecordFileInstance) : RecordFileInstance

    //Main implementation of the operator
    protected abstract fun execute(recordFile: RecordFileInstance) : RecordFileInstance
}