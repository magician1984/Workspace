package com.auo.dvr.launcher.filemanager

import android.util.Log
import com.auo.dvr.data.RecordFileBundle
import com.auo.dvr.launcher.DvrLauncher
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordType
import java.io.File
import java.util.EnumMap
import java.util.concurrent.locks.ReentrantLock

internal class FileManager internal constructor(private val injector: FileManagerInjector) :
    DvrLauncher.IFileManager {

    internal interface IFileParser {
        fun parseEvent(file: File): RecordFileBundle
        fun parseEventRecord(file: File): RecordFileBundle
        fun parseRecord(file: File): RecordFileBundle
    }

    internal interface IReverseParser {
        fun reverseParse(file : File, location : CamLocation, type : RecordType) : RecordFileBundle
    }

    internal interface IEventHandler {
        var onComplete: ((File) -> Unit)?
        fun handleEvent(
            event: RecordFileBundle,
            writingFile: RecordFileBundle,
            previousFileBundle: RecordFileBundle?
        )
    }

    interface IOperatorMethods {
        fun copy(src: File, dst: File)
        fun move(src: File, dst: File, isSamePartition: Boolean)
        fun delete(file: File)
    }

    internal interface IRepo {
        val files: List<RecordFileBundle>
        val root: File
        val operator: IOperatorMethods
        val reverseParser: IReverseParser

        fun init()
        fun release() {}
        fun clean()

        fun add(file: RecordFileBundle)
        fun remove(id: Int)
        fun get(id: Int): RecordFileBundle
        fun lock(file: RecordFileBundle)
        fun unlock(file: RecordFileBundle)
        fun export(file: RecordFileBundle, dest: File)
        fun deleteBy(predicate : (RecordFileBundle) -> Boolean) = mutableListOf<RecordFileBundle>().apply {
            addAll(files.filter(predicate))
        }.forEach { remove(it.id) }
    }

    internal interface ITriggerNotifier{
        var callback : ((predicate : (RecordFileBundle) -> Boolean) -> Unit)?
    }

    override var recordUpdateListener: DvrLauncher.IFileManager.RecordUpdateListener? = null

    override val recordFiles: List<RecordFile>
        get() = RecordFileBundle.toRecordFileList(mRepo.files)

    private val mRepo: IRepo
        get() = injector.repo

    private val mEventHandler: IEventHandler
        get() = injector.eventHandler

    private val mParser: IFileParser
        get() = injector.parser

    private var mHoldingRecord: RecordFileBundle? = null

    private val lock: ReentrantLock = ReentrantLock()

    private val completeCondition = lock.newCondition()

    private var mIsInitialized = false

    private val mCurrentRecordFile: EnumMap<CamLocation, RecordFileBundle?> =
        EnumMap<CamLocation, RecordFileBundle?>(
            CamLocation::class.java
        ).apply {
            CamLocation.entries.forEach { put(it, null) }
        }

    override fun init() {
        if(mIsInitialized)
            return

        mRepo.init()
        mEventHandler.onComplete = ::handleEvent

        mIsInitialized = true
    }

    override fun release() {
        if(!mIsInitialized)
            return

        mRepo.clean()
        mRepo.release()

        mIsInitialized = false
    }

    override fun copyFile(recordFile: RecordFile, destPath: String) {
        val recordFileBundle = findFileOrThrow(recordFile)

        if (mHoldingRecord != null && recordFileBundle != mHoldingRecord)
            throw FileManagerApiException("Copy", "Another file is on holding")

        lock.lock()

        try {
            mHoldingRecord = recordFileBundle

            injector.operator.copy(mHoldingRecord!!.file!!, File(destPath))

            completeCondition.signal()
        } finally {
            mHoldingRecord = null
            lock.unlock()
        }
    }

    override fun deleteFile(recordFile: RecordFile) = tryWaitForCopy(recordFile) {
        mRepo.remove(it.id)
        recordUpdateListener?.onUpdate()
    }

    override fun lockFile(recordFile: RecordFile) = tryWaitForCopy(recordFile) {
        mRepo.lock(it)
        recordUpdateListener?.onUpdate()
    }

    override fun unlockFile(recordFile: RecordFile) = tryWaitForCopy(recordFile) {
        mRepo.unlock(it)
        recordUpdateListener?.onUpdate()
    }

    override fun forceClone() {
        //TODO("Not yet implemented")
    }

    override fun onFileUpdate(
        eventType: DvrLauncher.IFileManager.EventType,
        type: DvrLauncher.IFileManager.FileType,
        file: File
    ) {
        Log.d("FileManager", "onFileUpdate: $eventType, $type, ${file.absolutePath}")
        if (eventType == DvrLauncher.IFileManager.EventType.Close) {
            when (type) {
                DvrLauncher.IFileManager.FileType.Event -> {
                    mParser.parseEvent(file).run {
                        val currentFileBundle = mCurrentRecordFile[this.location] ?: return@run
                        val previousFileBundle = mRepo.files.filter { item ->
                            item.location == this.location
                        }.maxByOrNull { it.createTime }
                        mEventHandler.handleEvent(this, currentFileBundle, previousFileBundle)
                    }
                }

                DvrLauncher.IFileManager.FileType.Record -> {
                    val recordFileBundle: RecordFileBundle = mParser.parseRecord(file)
                    mRepo.add(recordFileBundle)
                    recordUpdateListener?.onUpdate()
                }
            }
        } else {
            if (type == DvrLauncher.IFileManager.FileType.Record) {
                val recordFileBundle: RecordFileBundle = mParser.parseRecord(file)
                mCurrentRecordFile[recordFileBundle.location] = recordFileBundle
            }
        }
    }

    private fun handleEvent(file: File) {
        val record  = mParser.parseEventRecord(file)
        mRepo.add(record)
        recordUpdateListener?.onUpdate()
    }

    private fun findFileOrThrow(file: RecordFile): RecordFileBundle = mRepo.get(file.hashCode())

    private inline fun tryWaitForCopy(recordFile: RecordFile, block: (RecordFileBundle) -> Unit) {
        val recordFileBundle = findFileOrThrow(recordFile)

        if (recordFileBundle == mHoldingRecord) {
            try {
                lock.lock()

                completeCondition.await()
            } finally {
                lock.unlock()
            }
        }

        block(recordFileBundle)
    }
}