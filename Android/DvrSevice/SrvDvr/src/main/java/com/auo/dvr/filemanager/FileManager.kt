package com.auo.dvr.filemanager

import com.auo.dvr.DvrService
import com.auo.dvr.RecordFileInstance
import com.auo.dvr.filemanager.exception.FileOperatorException
import com.auo.dvr.filemanager.exception.StateFlowException
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.RecordFile
import java.io.File
import java.util.EnumMap

class FileManager internal constructor(private val injector: FileManagerInjector):
    DvrService.IFileManager {

    override var recordUpdateListener: DvrService.IFileManager.RecordUpdateListener? = null

    override val recordFiles: List<RecordFile>
        get() = mRepo.files.map { RecordFileInstance.toRecordFile(it) }

    private var mState : FileManagerState =
        FileManagerState.None

    private val mRepo : IRepo<*>
        get() = injector.repo

    private val mParser : IFileParser
        get() = injector.parser

    private var mHoldingRecord : RecordFileInstance? = null


    private val mCurrentRecordFile : EnumMap<CamLocation, RecordFileInstance?> = EnumMap<CamLocation, RecordFileInstance?>(
        CamLocation::class.java).apply {
        CamLocation.entries.forEach { put(it, null) }
    }

    @Throws(DvrException::class)
    override fun init() {
        stateFlow(expectState = FileManagerState.None, newState = FileManagerState.Ready){
            mRepo.init()
        }
    }

    override fun release() {
        stateFlow(expectState = FileManagerState.Ready, newState = FileManagerState.None) {
            mRepo.clean()
        }
    }

    override fun holdFile(recordFile: RecordFile) {
        val recordFileInstance = findFileOrThrow(recordFile)

        if(mHoldingRecord != null && mHoldingRecord?.id != recordFileInstance.id)
            throw FileOperatorException("Hold", "Another file is holding")

        if(mHoldingRecord?.id == recordFileInstance.id)
            return

        if(!mRepo.hold(recordFileInstance))
            throw FileOperatorException("Hold", "Failed to hold file")

        mHoldingRecord = recordFileInstance
    }

    override fun releaseFile(recordFile: RecordFile) {
        val recordFileInstance = findFileOrThrow(recordFile)

        if(mHoldingRecord == null || mHoldingRecord?.id != recordFileInstance.id)
            throw FileOperatorException("Release", "File is not holding")

        if(!mRepo.unhold(recordFileInstance))
            throw FileOperatorException("Release", "Failed to release file")

        mHoldingRecord = null
    }

    override fun getFilePath(recordFile: RecordFile): String = findFileOrThrow(recordFile).file?.absolutePath ?: throw FileOperatorException("GetFilePath", "File not found")

    override fun deleteFile(recordFile: RecordFile){
        val recordFileInstance = findFileOrThrow(recordFile)

        if(!mRepo.remove(recordFileInstance.id))
            throw FileOperatorException("Delete", "Failed to delete file")
    }

    override fun lockFile(recordFile: RecordFile){
        val recordFileInstance = findFileOrThrow(recordFile)

        if(!mRepo.lock(recordFileInstance))
            throw FileOperatorException("Lock", "Failed to lock file")
    }

    override fun unlockFile(recordFile: RecordFile){
        val recordFileInstance = findFileOrThrow(recordFile)

        if(!mRepo.unlock(recordFileInstance))
            throw FileOperatorException("Unlock", "Failed to unlock file")
    }

    override fun forceClone() {
        //TODO("Not yet implemented")
    }

    override fun onFileClosed(type: DvrService.IDvrLauncher.FileType, file: File) {
        TODO("Not yet implemented")
    }

    override fun onFileCreated(type: DvrService.IDvrLauncher.FileType, file: File) {
        TODO("Not yet implemented")
    }


    private inline fun stateFlow(expectState : FileManagerState, newState: FileManagerState, mainFunc : ()->Unit){
        if(mState != expectState)
            throw StateFlowException(mState, newState)

        mainFunc()

        mState = newState
    }

    private fun findFileOrThrow(file : RecordFile) : RecordFileInstance = mRepo.get(file.hashCode()) ?: throw FileNotExistException(file.name)
}