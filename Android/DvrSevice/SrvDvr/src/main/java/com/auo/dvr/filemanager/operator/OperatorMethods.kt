package com.auo.dvr.filemanager.operator

import com.auo.dvr.filemanager.FileManager
import com.auo.dvr.filemanager.FileManagerException
import java.io.File
import java.io.IOException

internal class OperatorMethods : FileManager.IOperatorMethods {
    override fun copy(src: File, dst: File) {
        fileCheck(src){
            try {
                src.copyTo(dst, true)
            }catch (e : IOException){
                throw FileOperatorException("Copy", "Copy failed: ${e.message}")
            }
        }
    }

    override fun move(src: File, dst: File, isSamePartition: Boolean){
        fileCheck(src){
            if(dst.exists())
                dst.delete()

            if(isSamePartition){
                if(!src.renameTo(dst))
                    throw FileOperatorException("Move", "Move failed: ${src.absolutePath} to ${dst.absolutePath}")
            }else{
                copy(src, dst)
                if(!src.delete())
                    throw FileOperatorException("Move", "Delete failed after copy: ${src.absolutePath}")
            }
        }
    }

    override fun delete(file: File) : Unit = if(file.delete()) Unit else throw FileOperatorException("Delete", "File not found: ${file.absolutePath}")

    private class FileOperatorException(method : String, message: String) : FileManagerException("[Operator]Method ($method) error: $message")

    private inline fun fileCheck(file: File, block: (File) -> Unit){
        if(!file.exists())
            throw FileOperatorException("Check", "File not found: ${file.absolutePath}")

        block(file)
    }
}