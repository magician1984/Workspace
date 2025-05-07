package com.auo.performancetester.datasource

import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.BlockStat
import com.auo.performancetester.domain.entity.TestCaseConfigure
import java.io.BufferedWriter
import java.io.File

object ResultWriter {
    private const val ROOT_NAME = "usb_test_result"
    private const val FILE_NAME_FORMAT = "%s.csv"

    fun disable(): IDataSource.IResultWriter = object : IDataSource.IResultWriter {
        override fun open(configure: TestCaseConfigure) {

        }

        override fun write(index: Int, stat: BlockStat) {
        }

        override fun close() {
        }
    }

    fun enable(root: File): IDataSource.IResultWriter{
        val rootDir = File(root, ROOT_NAME)
        if (rootDir.exists())
            rootDir.delete()
        rootDir.mkdirs()

        return object : IDataSource.IResultWriter {
            private val rootFolder: File = rootDir
            private var writer: BufferedWriter? = null
            private var openFile: File? = null

            init {
                if(rootFolder.exists())
                    rootFolder.delete()

                rootFolder.mkdirs()
            }

            override fun open(configure: TestCaseConfigure) {
                openFile = File(rootFolder, generateFileName(configure))
                writer = openFile!!.bufferedWriter()
                writer!!.write("index, ${BlockStat.getHeader()}\n")
            }

            override fun write(index: Int, stat: BlockStat) {
                writer?.write("$index, ${BlockStat.getValue(stat)}\n")
            }

            override fun close() {
                writer?.flush()
                writer?.close()
                writer = null
                openFile = null
            }

            private fun generateFileName(configure: TestCaseConfigure): String {
                return String.format(FILE_NAME_FORMAT, "${configure}_${System.currentTimeMillis()}")
            }
        }
    }
}