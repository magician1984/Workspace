package com.auo.dvr.manager.filemanager.parser

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.filemanager.standard.IFileParser
import com.auo.dvr.manager.filemanager.standard.exception.FileParserException
import com.auo.dvr_core.CamLocationDef
import com.auo.dvr_core.RecordFileTypeDef
import java.io.File

/**
 * Parser the filename to create RecordFileInstance
 * Filename format: {Location_id}_{CreateTime}.{Extension}
 */
internal class FileParser(
    targetRoot: HashMap<CamLocationDef, File>,
    protectFolderName: String,
    recordExtension: String,
    eventExtension: String
) : IFileParser(targetRoot, protectFolderName, recordExtension, eventExtension) {
    override fun parse(sourceFile: File): RecordFileInstance {
        return sourceFile.run {
            when {
                this.endsWith(recordExtension) -> parseRecordFile(this)
                this.endsWith(eventExtension) -> parseEventFile(this)
                else -> throw FileParserException(sourceFile)
            }
        }
    }

    override fun reverse(destFile: File): RecordFileInstance {
        TODO("Not yet implemented")
    }

    private fun parseRecordFile(sourceFile: File): RecordFileInstance {
        val filename: String = sourceFile.name

        val contents: List<String> = filename.removeSuffix(recordExtension).split('_')
        val locationDef: CamLocationDef =
            CamLocationDef.entries.find { it.value == contents[0].toByteOrNull() }
                ?: throw FileParserException(File(filename))
        val name: String = contents[1] + recordExtension
        val createTime: Long =
            contents[1].toLongOrNull() ?: throw FileParserException(File(filename))

        val info = RecordFileInstance.FileInfo(sourceFile, File(targetRoot[locationDef], name))

        return RecordFileInstance(
            name,
            locationDef,
            createTime,
            RecordFileTypeDef.Normal,
            false,
            info
        )

    }

    private fun parseEventFile(sourceFile: File): RecordFileInstance {
        TODO()
    }

}