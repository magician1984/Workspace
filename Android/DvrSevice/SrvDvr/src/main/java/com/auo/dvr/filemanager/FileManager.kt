package com.auo.dvr.filemanager

import com.auo.dvr.DvrService
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordFile
import java.io.File
import java.util.EnumMap
import java.util.concurrent.locks.ReentrantLock

internal class FileManager internal constructor(private val injector: FileManagerInjector) :
    DvrService.IFileManager {

    internal interface IFileParser {
        fun parseEvent(file: File): RecordFileBundle
        fun parseRecord(file: File): RecordFileBundle
    }

    internal interface IEventHandler {
        fun handleEvent(event: RecordFileBundle, records: List<RecordFileBundle>): RecordFileBundle
    }

    interface IOperatorMethods {
        fun copy(src: File, dst: File)
        fun move(src: File, dst: File, isSamePartition: Boolean)
        fun delete(file: File)
    }

    internal interface IRepo {
        val files: List<RecordFileBundle>
        val root: File

        fun init()
        fun release() {}
        fun clean()
        fun filter(predicate: (RecordFileBundle) -> Boolean): List<RecordFileBundle> = files.filter(predicate)
        fun add(file: RecordFileBundle): FileInfo
        fun remove(id: Int): FileInfo
        fun get(id: Int): RecordFileBundle
        fun lock(file: RecordFileBundle): FileInfo
        fun unlock(file: RecordFileBundle): FileInfo
    }

    internal interface ICache : IRepo{
        fun interface OnCacheFilesOutListener{
            fun onOut(files: List<RecordFileBundle>)
        }
    }

    override var recordUpdateListener: DvrService.IFileManager.RecordUpdateListener? = null

    override val recordFiles: List<RecordFile>
        get() = mRepo.files.map { RecordFileBundle.toRecordFile(it) }

    private var mState: FileManagerState =
        FileManagerState.None

    private val mRepo: IRepo
        get() = injector.repo

    private val mParser: IFileParser
        get() = injector.parser

    private var mHoldingRecord: RecordFileBundle? = null

    private val lock: ReentrantLock = ReentrantLock()

    private val completeCondition = lock.newCondition()

    private val mCurrentRecordFile: EnumMap<CamLocation, RecordFileBundle?> =
        EnumMap<CamLocation, RecordFileBundle?>(
            CamLocation::class.java
        ).apply {
            CamLocation.entries.forEach { put(it, null) }
        }

    override fun init() {
        stateFlow(expectState = FileManagerState.None, newState = FileManagerState.Ready) {
            mRepo.init()
        }
    }

    override fun release() {
        stateFlow(expectState = FileManagerState.Ready, newState = FileManagerState.None) {
            mRepo.clean()
            mRepo.release()
        }
    }

    override fun copyFile(recordFile: RecordFile, destPath: String) {
        val recordFileBundle = findFileOrThrow(recordFile)

        if (recordFileBundle != mHoldingRecord)
            throw FileManagerApiException("Copy", "File is holding by another thread")

        lock.lock()

        try{
            mHoldingRecord = recordFileBundle

            injector.operator.copy(mHoldingRecord!!.file!!, File(destPath))

            completeCondition.signal()
        }finally {
            mHoldingRecord = null
            lock.unlock()
        }

    }

    override fun deleteFile(recordFile: RecordFile) = tryWaitForCopy(recordFile){
        val fileInfo : FileInfo = mRepo.remove(it.id)
        injector.operator.delete(fileInfo.file)
    }

    override fun lockFile(recordFile: RecordFile) = tryWaitForCopy(recordFile){
        val fileInfo : FileInfo = mRepo.lock(it)
        injector.operator.move(it.file!!, fileInfo.file, true)
    }

    override fun unlockFile(recordFile: RecordFile) = tryWaitForCopy(recordFile){
        val fileInfo : FileInfo = mRepo.unlock(it)
        injector.operator.move(it.file!!, fileInfo.file, true)
    }

    override fun forceClone() {
        //TODO("Not yet implemented")
    }

    override fun onFileUpdate(
        eventType: DvrService.IDvrLauncher.EventType,
        type: DvrService.IDvrLauncher.FileType,
        file: File
    ) {
        if (eventType == DvrService.IDvrLauncher.EventType.Close) {
             when (type) {
                DvrService.IDvrLauncher.FileType.Event -> {
                    mParser.parseEvent(file).run {
                        TODO("Handle event file")
                    }
                }

                DvrService.IDvrLauncher.FileType.Record -> {
                    val recordFileBundle: RecordFileBundle = mParser.parseRecord(file)
                    mRepo.add(recordFileBundle)
                }
            }
        }else{
            if(type == DvrService.IDvrLauncher.FileType.Record){
                val recordFileBundle: RecordFileBundle = mParser.parseRecord(file)
                mCurrentRecordFile[recordFileBundle.location] = recordFileBundle
            }
        }
    }

    private inline fun stateFlow(
        expectState: FileManagerState,
        newState: FileManagerState,
        mainFunc: () -> Unit
    ) {
        if (mState != expectState)
            throw FileManagerStateFlowException(mState, newState)

        mainFunc()

        mState = newState
    }

    private fun findFileOrThrow(file: RecordFile): RecordFileBundle = mRepo.get(file.hashCode())

    private inline fun tryWaitForCopy(recordFile: RecordFile, block: (RecordFileBundle) -> Unit) {
        val recordFileBundle = findFileOrThrow(recordFile)

        if (recordFileBundle == mHoldingRecord){
            try{
                lock.lock()

                completeCondition.await()
            }finally {
                lock.unlock()
            }
        }

        block(recordFileBundle)
    }
}