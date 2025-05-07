package com.auo.performancetester.datasource

import android.content.Context
import android.util.Log
import com.auo.performancetester.datasource.method.BufferCopyMethod
import com.auo.performancetester.datasource.method.FileChannelMethod
import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.dvr_core.DvrException
import com.auo.performancetester.domain.entity.FileAllocateMode
import com.auo.performancetester.domain.entity.FileSize
import com.auo.performancetester.domain.entity.IData
import com.auo.performancetester.domain.entity.TestCaseConfigure
import java.io.File
import java.io.RandomAccessFile

class DataSource(private val context : Context, private val monitor : IDataSource.IPerformanceMonitor, private val writer : IDataSource.IResultWriter) : IDataSource {
    override var eventListener: IDataSource.EventListener? = null

    private var sourceFolder : File? = null

    private var targetFolder : File? = null

    override fun initialize() {
        notifyEvent("Start initialize")
        this.sourceFolder = File(File(context.filesDir, "files_src"), "source")

        if(this.sourceFolder?.exists() == false){
            this.sourceFolder!!.mkdirs()
        }

        notifyEvent("Source folder created: ${this.sourceFolder?.absolutePath}")

        notifyEvent("Query USB storage")
        val mountFolder = File("/mnt/media_rw")

        if(!mountFolder.exists()){
            notifyEvent("USB storage root not found")
            return
        }

        val list = mountFolder.listFiles()

        if(list == null || list.isEmpty()){
            notifyEvent("USB storage not found")
            return
        }

        val targetRoot = list[0]

        notifyEvent("USB storage found: ${targetRoot.absolutePath}")
        this.targetFolder = File(targetRoot, "target")

        if(this.targetFolder?.exists() == false){
            this.targetFolder!!.mkdirs()
        }

        notifyEvent("Initialize finished")
    }

    override fun startTest(configure: TestCaseConfigure) {
        initialCheck{ srcFolder, dstFolder ->
            clean(srcFolder, dstFolder)

            val methodImpl = when(configure.cloneMethod){
                CloneMethod.BufferIO -> BufferCopyMethod()
                CloneMethod.FileChannel -> FileChannelMethod()
            }

            val fileSize : Long = when(configure.fileSize){
                // 1M
                FileSize.Small -> 1024 * 1024
                // 20M
                FileSize.Medium -> 20 * 1024 * 1024
                // 100M
                FileSize.Large -> 100 * 1024 * 1024
            }

            val preallocate = configure.allocateMode == FileAllocateMode.PreAllocate

            notifyEvent("Start test: $configure")
            notifyEvent("Open log writer")
            writer.open(configure)
            notifyEvent("Create test file: $fileSize bytes")
            val srcFile = createTestFile(srcFolder, fileSize)
            notifyEvent("Create destination files: ${configure.fileCount} files")
            val destFiles = createDstFiles(dstFolder, configure.fileCount)
            val forceWrite = configure.forceWrite
            val startTime = System.nanoTime()
            for(i in destFiles.indices){
                monitor.start()
                methodImpl.clone(srcFile, destFiles[i], preallocate, forceWrite)
                monitor.stop()
                writer.write(i, monitor.getResult()!!)
            }
            val endTime = System.nanoTime()
            writer.close()
            notifyEvent("Test finished: $configure")
            notifyResult(IData.TestResult(fileSize, configure.fileCount, configure.cloneMethod,(endTime - startTime) / 1_000_000))
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

    private fun createTestFile(folder : File, size: Long): File{
        val file = File(folder, "test_file_${System.currentTimeMillis()}.tmp")
        RandomAccessFile(file, "rw").apply {
            setLength(size)
            close()
        }
        return file
    }

    private fun createDstFiles(folder : File, files : List<File>) : List<File>{
        val dstFiles : MutableList<File> = mutableListOf()

        files.forEach { file ->
            dstFiles.add(File(folder, file.name))
        }
        return dstFiles
    }

    private fun createDstFiles(folder : File, count : Int) : List<File>{
        val dstFiles : MutableList<File> = mutableListOf()
        for (i in 0 until count) {
            dstFiles.add(File(folder, "test_file_$i.tmp"))
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