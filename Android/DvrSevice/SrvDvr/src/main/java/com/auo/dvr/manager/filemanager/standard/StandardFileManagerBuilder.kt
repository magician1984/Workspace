package com.auo.dvr.manager.filemanager.standard

import com.auo.dvr.manager.filemanager.IFileManager
import com.auo.dvr.manager.filemanager.standard.detector.StandardFileDetector
import com.auo.dvr.manager.filemanager.standard.eventhandler.StandardEventFileHandler
import com.auo.dvr.manager.filemanager.standard.parser.FileParser
import com.auo.dvr.manager.filemanager.standard.pool.ListFilePool
import com.auo.dvr.manager.filemanager.standard.processor.MuxerFileProcessor
import com.auo.dvr.manager.filemanager.standard.processor.StandardFileProcessor
import javax.xml.transform.Source

class StandardFileManagerBuilder : IFileManager.Builder<StandardFileManager> {
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

    fun setSourceFileType(type : SourceFileType) : StandardFileManagerBuilder = this.apply { sourceFileType = type  }

    override fun build(): StandardFileManager {
        val injector : StandardFileManagerInjector = when(sourceFileType){
            SourceFileType.Mp4 -> StandardFileManagerInjector(
                processor = StandardFileProcessor(),
                parser = FileParser(),
                eventHandler = StandardEventFileHandler(),
                pool = ListFilePool(),
                fileDetector = StandardFileDetector(MEDIA_FILE_EXTENSION_MP4, EVENT_FILE_EXTENSION)
            )
            SourceFileType.Hevc -> StandardFileManagerInjector(
                processor = MuxerFileProcessor(),
                parser = FileParser(),
                eventHandler = StandardEventFileHandler(),
                pool = ListFilePool(),
                fileDetector = StandardFileDetector(MEDIA_FILE_EXTENSION_HEVC, EVENT_FILE_EXTENSION)
            )
        }

        return StandardFileManager(injector)
    }
}