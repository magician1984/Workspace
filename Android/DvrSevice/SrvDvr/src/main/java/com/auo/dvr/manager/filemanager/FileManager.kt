package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.IFileManager
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.RecordFile
import java.io.File
import java.util.EnumMap

class FileManager internal constructor(injector: FileManagerInjector): IFileManager {

    override var recordUpdateListener: IFileManager.RecordUpdateListener? = null

    override val recordFiles: List<RecordFile>
        get() = fileControl { pool ->
            return RecordFileInstance.toRecordFileList(pool.recordFiles)
        }

    private var mState : FileManagerState = FileManagerState.None

    private val mInjector : FileManagerInjector = injector

    private val mCurrentRecordFile : EnumMap<CamLocation, RecordFileInstance?> = EnumMap<CamLocation, RecordFileInstance?>(
        CamLocation::class.java).apply {
        CamLocation.entries.forEach { put(it, null) }
    }

    @Throws(DvrException::class)
    override fun init(config: FileManagerConfig) {
        stateFlow(expectState = FileManagerState.None, newState = FileManagerState.Ready){
            mInjector.apply {
                pool.clear()
                fileDetector.onFileCreated = this@FileManager::onFileCreated
                fileDetector.onFileClosed = this@FileManager::onFileClosed
            }
        }
    }

    override fun start() {
        stateFlow(expectState = FileManagerState.Ready, newState = FileManagerState.Running){
            mInjector.fileDetector.start()
        }
    }

    override fun stop() {
        stateFlow(expectState = FileManagerState.Running, newState = FileManagerState.Ready){
            mInjector.fileDetector.stop()
        }
    }

    override fun release() {
        stateFlow(expectState = FileManagerState.Ready, newState = FileManagerState.None) {
            mInjector.pool.clear()
        }
    }

    override fun getFilePath(recordFile: RecordFile): String = fileControl { pool ->
        pool.getRecordFile(recordFile.hashCode())?.file?.absolutePath ?: throw FileNotExistException(recordFile.name)
    }

    override fun deleteFile(recordFile: RecordFile): Unit = fileControl { pool ->
        pool.removeFile(RecordFileInstance.getHash(recordFile))
    }

    override fun changType(recordFile: RecordFile, type: RecordFileTypeDef) : Unit = fileControl { pool ->
        pool.getRecordFile(RecordFileInstance.getHash(recordFile))?.copy(typeDef = type)?.let { newRecordFile ->
            pool.updateFile(RecordFileInstance.getHash(recordFile), newRecordFile)
        }
    }

    override fun lockFile(recordFile: RecordFile) : Unit = fileControl {pool->
        pool.getRecordFile(RecordFileInstance.getHash(recordFile))?.let { recordFile ->
            val newFile = recordFile.copy(state = recordFile.state.copy(isLocked = true))

        }
    }

    override fun unlockFile(recordFile: RecordFile) : Unit = fileControl{

        it.updateFile(RecordFileInstance.getHash(recordFile), newFile)
    }

    override fun forceClone() {
        TODO("Not yet implemented")
    }

    private inline fun stateFlow(expectState : FileManagerState, newState: FileManagerState, mainFunc : ()->Unit){
        if(mState != expectState)
            throw StateFlowException(mState, newState)

        mainFunc()

        mState = newState
    }

    private inline fun <T> fileControl(mainFunc : (pool : IFilePool)->T) : T{
        if(mState == FileManagerState.None)
            throw StateAssertionException(mState, FileManagerState.None, isNot = true)

        val pool = mInjector.pool

        return mainFunc(pool)
    }

    /**
     * When provider create a file
     */
    private fun onFileCreated(file : File){
        val recordFile = mInjector.parser.parse(file)

        if(recordFile.recordFileType == RecordFileTypeDef.Event)
            return

        mCurrentRecordFile[recordFile.camLocation] = recordFile
    }

    /**
     * When provider write complete
     */
    private fun onFileClosed(file : File){
        val recordFile = mInjector.parser.parse(file)

        if(recordFile.recordFileType == RecordFileTypeDef.Event){
            mInjector.eventHandler.run {
                // Find last record file in pool
                val lastRecordFile : RecordFileInstance? = mInjector.pool.getRecordsByLocation(recordFile.camLocation).lastOrNull()
                process(recordFile, mCurrentRecordFile[recordFile.camLocation], lastRecordFile)
            }
            return
        }

        mCurrentRecordFile[recordFile.camLocation] = null

        mInjector.processor.clone(recordFile)

        recordUpdateListener?.onUpdate()
    }

}