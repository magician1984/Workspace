package com.auo.dvr.recordmanager.convertor

import android.net.Uri
import com.auo.dvr.recordmanager.IConvertor
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import java.io.File

/***
 * Convert folder to RecordGroup
 * the folder must be a directory
 * @param folder the folder to convert
 * @param overrideType override the type of the RecordGroup, used for restore on initial
 * @return the RecordGroup
 * @throws ParseException if parse failed
 * the folder name format is {timestamp} or {timestamp}_evt
 */
internal class FileConvertor : IConvertor {
    companion object{
        private const val TAG : String = "FileConvertor"
        private const val EVENT_FOLDER_EXTENSION : String = "evt"
        private const val VIDEO_FILE_EXTENSION : String = "ts"
        private const val THUMBNAIL_FILE_EXTENSION : String = "jpg"
    }

    override fun parse(folder: File, overrideType : RecordType?): RecordGroup {
        if(folder.isFile)
            throw ParseException("folder is file")



        val videoFiles = folder.listFiles { file -> file.extension == VIDEO_FILE_EXTENSION } ?: throw ParseException("no video files")
        val thumbnailFiles = folder.listFiles { file -> file.extension == THUMBNAIL_FILE_EXTENSION } ?: throw ParseException("no thumbnail files")


        try{
            val timestamp : Long = folder.name.removeSuffix("_$EVENT_FOLDER_EXTENSION").toLong()

            if(videoFiles.size != thumbnailFiles.size)
                throw ParseException("video files and thumbnail files are not equal")

            val records : List<RecordFile> = buildList {
                videoFiles.forEach { file ->

                    val name = file.nameWithoutExtension

                    val thumbnailFile = thumbnailFiles.find { it.nameWithoutExtension == name } ?: throw ParseException("no thumbnail file")

                    val location = CamLocation.fromName(name) ?: throw ParseException("invalid location")

                    val recordFile = RecordFile(name = name, createTime = timestamp, location = location, uri = Uri.fromFile(file), thumbnail = Uri.fromFile(thumbnailFile))

                    this.add(recordFile)
                }
            }

            val type = overrideType ?: if(folder.name.endsWith("_$EVENT_FOLDER_EXTENSION")) RecordType.Protected else RecordType.Normal

            return RecordGroup(timestamp = timestamp, files = records, type = type, uri = Uri.fromFile(folder))
        }catch (e : NumberFormatException){
            throw ParseException("folder name is invalid")
        }

    }

    inner class ParseException(massage : String) : DvrException(tag = TAG, msg = "Parse failed: $massage")
}