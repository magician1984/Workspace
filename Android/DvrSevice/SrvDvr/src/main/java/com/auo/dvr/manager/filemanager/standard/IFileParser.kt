package com.auo.dvr.manager.filemanager.standard

import com.auo.dvr.manager.filemanager.RecordFileInstance
import java.io.File

/**
 * Parser the filename to create RecordFileInstance
 * Locate the file in the target folder
 */
internal interface IFileParser {
    fun setDestRoot(root : File)
    fun parse(sourceFile : File) : RecordFileInstance
    fun reverse(destFile : File) : RecordFileInstance
}
