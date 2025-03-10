package com.auo.dvr.manager.filemanager

import com.auo.dvr.manager.filemanager.standard.FileManagerState
import com.auo.dvr_core.DvrException
import java.io.File

abstract class FileManagerException(message : String) : DvrException("FileManager", message)



class FileNotExistException(path : String) : FileManagerException("File not exist: $path")

class EventFileException(val file : File) : FileManagerException("Get event file: ${file.name}")