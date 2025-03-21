package com.auo.dvr.manager.filemanager.parser

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.CloneInfo
import com.auo.dvr.manager.filemanager.IFileParser
import com.auo.dvr.manager.filemanager.exception.FileParserException
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.FileType
import com.auo.dvr_core.RecordFile
import java.io.File

/**
 * Parser the filename to create RecordFileInstance
 * Filename format: {Location_id}_{CreateTime}.{Extension}
 */
internal class FileParser(
    targetRoot: HashMap<CamLocation, File>,
    protectFolderName: String,
    recordExtension: String,
    eventExtension: String
) : IFileParser(targetRoot, protectFolderName, recordExtension, eventExtension) {
    private enum class Type{
        Record,
        Event
    }

    override fun parse(sourceFile: File): RecordFileInstance {
        val info : Triple<CamLocation, Long, Type> = parseInfo(sourceFile.name)

        return when(info.third){
            Type.Record -> parseRecordFile(sourceFile, info.first, info.second)
            Type.Event -> parseEventFile(sourceFile, info.first, info.second)
        }
    }

    override fun reverse(destFile: File): RecordFileInstance {
        TODO("Not yet implemented")
    }

    private fun parseRecordFile(sourceFile: File, location: CamLocation, createTime: Long): RecordFileInstance {
        val name : String =  "$createTime.$recordExtension"

        val info = CloneInfo(false, sourceFile, File(targetRoot[location], name))

        return RecordFileInstance(RecordFile(name, createTime, location, FileType.Normal), info)
    }

    private fun parseEventFile(sourceFile: File, location: CamLocation, createTime: Long): RecordFileInstance {
        TODO()
    }

    private fun parseInfo(filename : String): Triple<CamLocation, Long, Type>{
        val type : Type = when(filename.substringAfterLast('.')){
            recordExtension -> Type.Record
            eventExtension -> Type.Event
            else -> throw FileParserException(File(filename))
        }

        val content :List<String> = filename.substringBeforeLast('.').split('_')

        val locationDef : CamLocation = CamLocation.entries.find { it.code == content[0].toIntOrNull() } ?: throw FileParserException(File(filename))

        val createTime : Long = content[1].toLongOrNull() ?: throw FileParserException(File(filename))

        return Triple(locationDef, createTime, type)
    }
}