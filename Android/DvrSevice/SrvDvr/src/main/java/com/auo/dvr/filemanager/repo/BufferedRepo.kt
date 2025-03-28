package com.auo.dvr.filemanager.repo

import com.auo.dvr.filemanager.FileInfo
import com.auo.dvr.filemanager.FileManager
import com.auo.dvr.filemanager.FileManagerException
import com.auo.dvr.filemanager.RecordFileBundle
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordType
import java.io.File

internal class BufferedRepo(override val root: File) : FileManager.IRepo {
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

    override fun add(file: RecordFileBundle): FileInfo {
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

        _files.add(file.copy(info = fileInfo))

        return fileInfo
    }

    override fun remove(id: Int): FileInfo {
        val file : RecordFileBundle = _files.find { it.id == id } ?: throw BufferRepoException("Remove", "File not found: $id")

        _files.remove(file)

        return file.info as FileInfo
    }

    override fun get(id: Int): RecordFileBundle {
        return _files.find { it.id == id } ?: throw BufferRepoException("Get", "File not found: $id")
    }

    override fun lock(file: RecordFileBundle): FileInfo {
        val index  = _files.indexOfFirst { it.id == file.hashCode() }

        if(index == -1)
            throw BufferRepoException("Lock", "File not found: ${file.name}")

        val recordFile : RecordFileBundle = _files[index]

        if(recordFile.type == RecordType.Locked)
            throw BufferRepoException("Lock", "File already locked: ${file.name}")

        return withLockFolder(file.location){
            val fileInfo = FileInfo(File(it, file.name))
            _files[index] = recordFile.copy(info = fileInfo)
            fileInfo
        }
    }

    override fun unlock(file: RecordFileBundle): FileInfo{
        val index = _files.indexOfFirst { it.id == file.hashCode() }

        if(index == -1)
            throw BufferRepoException("Unlock", "File not found: ${file.name}")

        val recordFile : RecordFileBundle = _files[index]

        if(recordFile.type != RecordType.Locked)
            throw BufferRepoException("Unlock", "File not locked: ${file.name}")

        return withCameraFolder(file.location){
            val fileInfo = FileInfo(File(it, file.name))
            _files[index] = file.copy(info = fileInfo)
            fileInfo
        }
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