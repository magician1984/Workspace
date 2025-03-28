package com.auo.dvr.filemanager.parser

import com.auo.dvr.filemanager.RecordFileBundle
import com.auo.dvr.filemanager.EventInfo
import com.auo.dvr.filemanager.FileInfo
import com.auo.dvr.filemanager.FileManager
import com.auo.dvr.filemanager.FileManagerException
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordType
import java.io.File

/**
 * Parser the filename to create RecordFileInstance
 * Filename format: {Location_id}_{CreateTime}.{Extension}
 */
internal class FileParser : FileManager.IFileParser {
    private data class InfoBundle(val location: CamLocation, val createTime: Long)

    override fun parseEvent(file: File): RecordFileBundle {
        val (location, createTime) = parseFileName(file.name)

        val recordFile = RecordFile(file.name, createTime, location, RecordType.Unknown)

        return RecordFileBundle(recordFile, EventInfo(createTime, 0))
    }

    override fun parseRecord(file: File): RecordFileBundle {
        val (location, createTime) = parseFileName(file.name)

        val recordFile = RecordFile(file.name.substringAfterLast('_'), createTime, location, RecordType.Normal)

        return RecordFileBundle(recordFile, FileInfo(file))
    }

    private fun parseFileName(filename: String) : InfoBundle{
        val content :List<String> = filename.substringBeforeLast('.').split('_')
        val locationDef : CamLocation = CamLocation.entries.find { it.code == content[0].toIntOrNull() } ?: throw FileParserException(File(filename))
        val createTime : Long = content[1].toLongOrNull() ?: throw FileParserException(File(filename))
        return InfoBundle(locationDef, createTime)
    }

    private class FileParserException(file : File) : FileManagerException("[Parser] Parse failed(${file.absolutePath})")
}