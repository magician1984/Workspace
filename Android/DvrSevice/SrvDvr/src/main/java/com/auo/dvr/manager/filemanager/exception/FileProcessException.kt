package com.auo.dvr.manager.filemanager.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import java.io.File

class FileProcessException(doWhat : String, method: String, message : String) : FileManagerException("$doWhat failed with method $method: $message"){
    constructor(src : File, dst: File, method: String, message : String) : this("Process file(${src.absolutePath} -> ${dst.absolutePath})", method, message)
}