package com.auo.dvr.filemanager.repo

import android.util.Log
import com.auo.dvr.filemanager.FileInfo
import com.auo.dvr.filemanager.FileManager
import com.auo.dvr.filemanager.FileManagerException
import com.auo.dvr.filemanager.RecordFileBundle
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordType
import java.io.File

internal class BufferedRepo(override val root: File,
                            override val operator: FileManager.IOperatorMethods
) : FileManager.IRepo {
    companion object {
        private const val LOCK_FOLDER_NAME = "Locked"
        private const val PROTECTED_FOLDER_NAME = "Protected"
    }

    override val files: List<RecordFileBundle>
        get() = _files

    private val _files : MutableList<RecordFileBundle> = mutableListOf()

    override fun init() {
        if (!root.exists())
            root.mkdirs()

        CamLocation.entries.forEach {
            val folder = File(root, it.name)
            if (!folder.exists())
                folder.mkdirs()

            val lockFolder = File(folder, LOCK_FOLDER_NAME)
            if (!lockFolder.exists())
                lockFolder.mkdirs()

            val protectedFolder = File(folder, PROTECTED_FOLDER_NAME)
            if (!protectedFolder.exists())
                protectedFolder.mkdirs()
        }
    }


    override fun clean() {
        _files.clear()
    }

    override fun add(file: RecordFileBundle) {
        if(_files.any { it.id == file.hashCode() })
            throw BufferRepoException("Add", "File already exists: ${file.name}")

        val fileInfo : FileInfo = if(file.type == RecordType.Protected){
            withProtectedFolder {
                FileInfo(File(it, file.name))
            }
        }else{
            withCameraFolder(file.location){
                FileInfo(File(it, file.name))
            }
        }

        operator.move(file.file!!, fileInfo.file, false)

        _files.add(file.copy(info = fileInfo))
    }

    override fun remove(id: Int) {
        val file : RecordFileBundle = _files.find { it.id == id } ?: throw BufferRepoException("Remove", "File not found: $id")

        operator.delete(file.file!!)

        _files.remove(file)

    }

    override fun get(id: Int): RecordFileBundle {
        return _files.find { it.id == id } ?: throw BufferRepoException("Get", "File not found: $id")
    }

    override fun lock(file: RecordFileBundle) {
        val index  = _files.indexOfFirst { it.id == file.hashCode() }

        if(index == -1)
            throw BufferRepoException("Lock", "File not found: ${file.name}")

        val recordFile : RecordFileBundle = _files[index]

        if(recordFile.type == RecordType.Locked)
            throw BufferRepoException("Lock", "File already locked: ${file.name}")

        val fileInfo = withLockFolder(file.location){
           FileInfo(File(it, file.name))
        }

        operator.move(recordFile.file!!, fileInfo.file, true)

        Log.d("BufferedRepo", "lock: ${recordFile.file!!.absolutePath} -> ${fileInfo.file.absolutePath}")

        _files[index] = recordFile.copy(recordFile = recordFile.recordFile.copy(type = RecordType.Locked), info = fileInfo)
    }

    override fun unlock(file: RecordFileBundle){
        val index = _files.indexOfFirst { it.id == file.hashCode() }

        if(index == -1)
            throw BufferRepoException("Unlock", "File not found: ${file.name}")

        val recordFile : RecordFileBundle = _files[index]

        if(recordFile.type != RecordType.Locked)
            throw BufferRepoException("Unlock", "File not locked: ${file.name}")

        val fileInfo = withCameraFolder(file.location){
            FileInfo(File(it, file.name))
        }

        operator.move(recordFile.file!!, fileInfo.file, true)

        Log.d("BufferedRepo", "unlock: ${recordFile.file!!.absolutePath} -> ${fileInfo.file.absolutePath}")

        _files[index] = recordFile.copy(recordFile = recordFile.recordFile.copy(type = RecordType.Normal), info = fileInfo)
    }

    private inline fun <R> withCameraFolder(camLocation: CamLocation, func: (File) -> R): R {
        val folder = File(root, camLocation.name)
        return func(folder)
    }

    private inline fun <R> withLockFolder(camLocation: CamLocation, func: (File) -> R): R{
        val folder = File(root, "${camLocation.name}/$LOCK_FOLDER_NAME")
        return func(folder)
    }

    private inline fun <R> withProtectedFolder(func : (File)->R) : R{
        val folder = File(root, PROTECTED_FOLDER_NAME)
        return func(folder)
    }

    private class BufferRepoException(doWhat : String, message : String) : FileManagerException("[Repo][$doWhat]$message")
}