package com.auo.dvr.filemanager

import android.os.Environment
import com.auo.dvr.DvrService
import com.auo.dvr.filemanager.event.EventHandler
import com.auo.dvr.filemanager.operator.OperatorMethods
import com.auo.dvr.filemanager.parser.FileParser
import com.auo.dvr.filemanager.repo.BufferedRepo
import com.auo.dvr.filemanager.trigger.DailyTrigger
import java.io.File

class FileManagerBuilder : DvrService.IFileManager.Builder {

    private var targetRoot: File = Environment.getDataDirectory()

    private var eventCacheRoot: File = Environment.getDataDirectory()

    fun setTargetRoot(targetRoot: File) = this.apply { this.targetRoot = targetRoot }

    fun setEventCacheRoot(eventCacheRoot: File) =
        this.apply { this.eventCacheRoot = eventCacheRoot }

    override fun build(): DvrService.IFileManager {
        // Check and Create target root

        val operatorMethods = OperatorMethods()

        val parser: FileParser = FileParser()

        val injector = FileManagerInjector(
            parser,
            BufferedRepo(targetRoot, operatorMethods, parser),
            operatorMethods,
            EventHandler(eventCacheRoot),
            DailyTrigger(3)
        )

        return FileManager(injector)
    }
}