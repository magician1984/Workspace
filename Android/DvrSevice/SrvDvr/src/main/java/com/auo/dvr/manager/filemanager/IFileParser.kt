package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import com.auo.dvr_core.CamLocation
import java.io.File

/**
 * Parser the filename to create RecordFileInstance
 * Locate the file in the target folder
 */
internal abstract class IFileParser(protected val targetRoot : HashMap<CamLocation, File>, protected val protectFolderName : String, protected val recordExtension : String, protected val eventExtension : String) {
    abstract fun parse(sourceFile : File) : RecordFileInstance
    abstract fun reverse(destFile : File) : RecordFileInstance
}
