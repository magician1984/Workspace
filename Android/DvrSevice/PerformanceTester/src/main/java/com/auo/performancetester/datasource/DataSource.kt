package com.auo.performancetester.datasource

import android.content.Context
import android.util.Log
import com.auo.performancetester.datasource.method.BufferCopyMethod
import com.auo.performancetester.datasource.method.FileChannelMethod
import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.dvr_core.DvrException
import com.auo.performancetester.domain.entity.BlockStat
import com.auo.performancetester.domain.entity.IData
import java.io.File
import java.io.RandomAccessFile

class DataSource(private val context : Context, private val monitor : IDataSource.IPerformanceMonitor) : IDataSource {
    override var eventListener: IDataSource.EventListener? = null

    private var sourceFolder : File? = null

    private var targetFolder : File? = null

    override fun initialize() {
        notifyEvent("Start initialize")
        this.sourceFolder = File(File(context.filesDir, "files_src"), "source")
        notifyEvent("Source folder created: ${this.sourceFolder?.absolutePath}")

        notifyEvent("Query USB storage")
        val mountFolder = File("/mnt/media_rw")

        if(!mountFolder.exists()){
            notifyEvent("USB storage root not found")
            return
        }

        mountFolder.listFiles().apply {
            if(this.isNullOrEmpty()){
                notifyEvent("USB storage not found")
                return
            }

            notifyEvent("Use USB storage: ${this[0].absolutePath}")

            this@DataSource.targetFolder = File(this[0], "target")

            notifyEvent("Initialize finished")
        }


    }

    override fun startTest(method:CloneMethod, size : Long, count : Int) {
        initialCheck{ src, dst ->
            notifyEvent("Select clone method")
            val methodImpl = when(method){
                CloneMethod.BufferIO -> BufferCopyMethod()
                CloneMethod.FileChannel -> FileChannelMethod()
                else -> throw DvrException(this.javaClass.name, "Unsupported")
            }

            notifyEvent("Clean up")
            clean(src, dst)

            notifyEvent("Create test files and dst files")
            val files : List<File> = createdTestFiles(src, size, count)
            val dstFiles : List<File> = createDstFiles(dst, files)

            notifyEvent("Start clone test")
            val startTime : Long = System.nanoTime()

            monitor.start()
            methodImpl.clone(files, dstFiles)
            monitor.stop()

            val endTime : Long = System.nanoTime()

            val totalTimeNano = startTime - endTime;
            notifyEvent("Clone test finished: $totalTimeNano us")

            notifyResult(IData.TestResult(size, count, method, totalTimeNano, monitor.getResult()))
        }
    }

     private fun clean(src:File, dst:File) {
        if(src.exists())
            src.deleteRecursively()

        if(dst.exists())
            dst.deleteRecursively()

        src.mkdirs()
        dst.mkdirs()
    }

    private fun createdTestFiles(folder : File, size: Long, count: Int): List<File>{
        val files : MutableList<File> = mutableListOf()

        var file: File

        for (i in 0 until count) {
            file = File(folder, "test_file_$i.tmp")
            RandomAccessFile(file, "rw").apply {
                setLength(size)
                close()
            }
            files.add(file)
        }

        return files
    }

    private fun createDstFiles(folder : File, files : List<File>) : List<File>{
        val dstFiles : MutableList<File> = mutableListOf()

        files.forEach { file ->
            dstFiles.add(File(folder, file.name))
        }
        return dstFiles
    }

    private fun notifyResult(result:IData.TestResult){
        Log.d("TestResult", result.toString())
        this.eventListener?.onEvent(result)
    }

    private fun notifyEvent(msg: String){
        Log.d("DataSource", msg)
        this.eventListener?.onEvent(IData.EventMessage(msg))
    }

    private inline fun initialCheck(func: (File, File)->Unit){
        if(this.sourceFolder == null || this.targetFolder == null)
            notifyEvent("DataSource not initialized")
        else{
            try{
                func(this.sourceFolder!!, this.targetFolder!!)
            }catch (e : DvrException){
                notifyEvent("Exception happened: $e")
            }
        }
    }
}