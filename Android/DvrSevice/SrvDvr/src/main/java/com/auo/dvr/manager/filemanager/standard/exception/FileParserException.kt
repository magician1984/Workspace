package com.auo.dvr.manager.filemanager.standard.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import java.io.File

class FileParserException(file : File) : FileManagerException("File ${file.absolutePath} parse failed")

