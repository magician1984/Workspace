package com.auo.performancetester.datasource

import com.auo.performancetester.datasource.method.BufferCopyMethod
import com.auo.performancetester.datasource.method.FileChannelMethod
import com.auo.performancetester.domain.datasource.ITestSource
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.DvrException
import com.auo.performancetester.domain.entity.IData
import java.io.File
import java.io.RandomAccessFile

class TestSource(private val sourceFolder : File, private val targetFolder : File) : ITestSource {
    override var eventListener: ITestSource.EventListener? = null

    override val results: List<IData.TestResult>
        get() = mResults

    private var mResults : MutableList<IData.TestResult> = mutableListOf()

    override fun startTest(method:CloneMethod, size : Long, count : Int) {
        notifyEvent("Select clone method")
        val methodImpl = when(method){
            CloneMethod.BufferIO -> BufferCopyMethod()
            CloneMethod.FileChannel -> FileChannelMethod()
            else -> throw DvrException(this.javaClass.name, "Unsupported")
        }

        notifyEvent("Clean up")
        clean()

        notifyEvent("Create test files")
        val files : List<File> = createdTestFiles(size, count)

        val times = mutableListOf<Long>()

        notifyEvent("Start clone test")
        for (file in files) {
            val startTime : Long = System.nanoTime()
            methodImpl.clone(file, File(this.targetFolder, file.name))
            val endTime : Long = System.nanoTime()
            times.add(endTime - startTime)
        }

        notifyEvent("Clone test finished")

        IData.TestResult(size, count, method, times.sum()).apply {
            mResults.add(this)
            notifyResult(this)
        }
    }

     private fun clean() {
        if(this.sourceFolder.exists())
            this.sourceFolder.deleteRecursively()

        if(this.targetFolder.exists())
            this.targetFolder.deleteRecursively()

        sourceFolder.mkdirs()
        targetFolder.mkdirs()
    }

    private fun createdTestFiles(size: Long, count: Int): List<File>{
        val files : MutableList<File> = mutableListOf()

        var file: File

        for (i in 0 until count) {
            file = File(sourceFolder, "test_file_$i.tmp")
            RandomAccessFile(file, "rw").apply {
                setLength(size)
                close()
            }
            files.add(file)
        }

        return files
    }

    private fun notifyResult(result:IData.TestResult) = this.eventListener?.onEvent(result)

    private fun notifyEvent(msg: String) = this.eventListener?.onEvent(IData.EventMessage(msg))
}