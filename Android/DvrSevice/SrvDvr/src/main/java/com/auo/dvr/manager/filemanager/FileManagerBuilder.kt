package com.auo.dvr.manager.filemanager

import android.os.Environment
import com.auo.dvr.manager.IFileManager
import com.auo.dvr.manager.filemanager.standard.detector.StandardFileDetector
import com.auo.dvr.manager.filemanager.standard.eventhandler.StandardEventFileHandler
import com.auo.dvr.manager.filemanager.standard.parser.FileParser
import com.auo.dvr.manager.filemanager.standard.processor.MuxerFileProcessor
import com.auo.dvr.manager.filemanager.standard.processor.StandardFileProcessor
import com.auo.dvr_core.CamLocationDef
import com.auo.dvr_core.RecordFileTypeDef
import java.io.File

class FileManagerBuilder : IFileManager.Builder<FileManager> {
    companion object{
        private const val MEDIA_FILE_EXTENSION_MP4 = ".mp4"
        private const val MEDIA_FILE_EXTENSION_HEVC = ".h265"

        private const val EVENT_FILE_EXTENSION = ".evt"
    }

    enum class SourceFileType{
        Mp4,
        Hevc
    }

    private var sourceFileType : SourceFileType = SourceFileType.Mp4

    private var sourceRoot: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Dvr_src")

    private var targetRoot: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Dvr_dst")

    fun setSourceFileType(type : SourceFileType) : FileManagerBuilder = this.apply { sourceFileType = type  }

    fun setSourceRoot(file : File) : FileManagerBuilder = this.apply { sourceRoot = file  }

    override fun build(): FileManager {
        // Check and Create target root
        if(!sourceRoot.exists()){
            if(!sourceRoot.mkdirs())
                throw com.auo.dvr.manager.filemanager.exception.FolderControlFailedException(
                    sourceRoot
                )
        }


        val subFolders : HashMap<CamLocationDef, File> = hashMapOf<CamLocationDef, File>().apply {
            CamLocationDef.entries.forEach { location->
                val folder = File(targetRoot, location.name)
                if(!folder.exists()){
                    if(!folder.mkdirs())
                        throw com.auo.dvr.manager.filemanager.exception.FolderControlFailedException(
                            folder
                        )

                    val eventFolder = File(folder, RecordFileTypeDef.Protect.name)

                    if(!eventFolder.mkdirs())
                        throw com.auo.dvr.manager.filemanager.exception.FolderControlFailedException(
                            eventFolder
                        )

                    this[location] = folder
                }
            }
        }

        val injector : FileManagerInjector = when(sourceFileType){
            SourceFileType.Mp4 -> FileManagerInjector(
                processor = StandardFileProcessor(),
                parser = FileParser(subFolders, MEDIA_FILE_EXTENSION_MP4, EVENT_FILE_EXTENSION),
                eventHandler = StandardEventFileHandler(),
                pool = com.auo.dvr.manager.filemanager.pool.ListFilePool(),
                fileDetector = StandardFileDetector(sourceRoot, MEDIA_FILE_EXTENSION_MP4, EVENT_FILE_EXTENSION)
            )
            SourceFileType.Hevc -> FileManagerInjector(
                processor = MuxerFileProcessor(),
                parser = FileParser(subFolders, MEDIA_FILE_EXTENSION_HEVC, EVENT_FILE_EXTENSION),
                eventHandler = StandardEventFileHandler(),
                pool = com.auo.dvr.manager.filemanager.pool.ListFilePool(),
                fileDetector = StandardFileDetector(sourceRoot, MEDIA_FILE_EXTENSION_HEVC, EVENT_FILE_EXTENSION)
            )
        }

        return FileManager(injector)
    }
}