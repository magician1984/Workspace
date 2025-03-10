package com.auo.dvr.manager.filemanager.standard.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import java.io.File

class FileProcessException(src : File, dst: File, method: String, message : String) : FileManagerException("File ${src.absolutePath} process to ${dst.absolutePath} failed with method $method: $message")