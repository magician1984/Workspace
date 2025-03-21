package com.auo.dvr.manager.filemanager.detector

import android.os.FileObserver
import com.auo.dvr.manager.filemanager.IFileDetector
import java.io.File

open class FileDetector(private val root : File, private val fileExtension:String, private val eventFileExtension:String) :
    IFileDetector {

    override var onFileCreated: ((file: File) -> Unit)? = null

    override var onFileClosed: ((file: File) -> Unit)? = null

    private val writingFiles = mutableListOf<File>()

    private val mFileObserver : FileObserver = object : FileObserver(root.absolutePath, CREATE or CLOSE_WRITE){
        override fun onEvent(event: Int, path: String?) {
            if(path == null || !(path.endsWith(eventFileExtension) || path.endsWith(fileExtension))) return

            if(event == CREATE){
                if(path.endsWith(fileExtension))
                    writingFiles.add(File(root, path))
                onFileCreated?.let { it(File(root, path)) }
            }

            else if(event == CLOSE_WRITE){
                if(path.endsWith(fileExtension))
                    writingFiles.remove(File(root, path))
                onFileClosed?.let { it(File(root, path)) }
            }

        }
    }

    override fun start() {
        mFileObserver.startWatching()
    }

    override fun stop() {
        mFileObserver.stopWatching()
    }

    override fun getWritingFiles(): List<File> {
        return writingFiles
    }

    override fun release() {
        writingFiles.clear()
    }




}