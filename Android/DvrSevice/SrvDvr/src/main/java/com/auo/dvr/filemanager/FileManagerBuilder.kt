package com.auo.dvr.filemanager

import android.os.Environment
import com.auo.dvr.DvrService
import com.auo.dvr.filemanager.event.EventHandler
import com.auo.dvr.filemanager.operator.OperatorMethods
import com.auo.dvr.filemanager.parser.FileParser
import com.auo.dvr.filemanager.repo.BufferedRepo
import java.io.File

class FileManagerBuilder : DvrService.IFileManager.Builder {

    private var targetRoot: File = Environment.getDataDirectory()

    fun setTargetRoot(targetRoot: File) = this.apply { this.targetRoot = targetRoot }

    override fun build(): DvrService.IFileManager {
        // Check and Create target root

        val operatorMethods = OperatorMethods()

        val injector = FileManagerInjector(FileParser(), BufferedRepo(targetRoot, operatorMethods), operatorMethods, EventHandler(Environment.getDataDirectory()))

        return FileManager(injector)
    }
}