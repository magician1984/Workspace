package com.auo.dvr.manager.filemanager

import com.auo.dvr.RecordFileInstance
import com.auo.dvr.manager.IFileManager
import com.auo.dvr.manager.filemanager.exception.FileOperatorException
import com.auo.dvr.manager.filemanager.exception.StateFlowException
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.FileType
import com.auo.dvr_core.RecordFile
import java.io.File
import java.util.EnumMap

class FileManager internal constructor(injector: FileManagerInjector): IFileManager {

    override var recordUpdateListener: IFileManager.RecordUpdateListener? = null

    override val recordFiles: List<RecordFile>
        get() = mInjector.repo.files.map {
            RecordFileInstance.toRecordFile(it)
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
            mInjector.repo.clean()
        }
    }

    override fun holdFile(recordFile: RecordFile) {
        val recordFileInstance = mInjector.repo.get(recordFile.hashCode())
            ?: throw FileNotExistException(recordFile.name)

        if(!mInjector.repo.lock(recordFileInstance))
            throw FileOperatorException("Lock", "Lock file failed")
    }

    override fun releaseFile(recordFile: RecordFile) {
        val recordFileInstance = mInjector.repo.get(recordFile.hashCode())
            ?: throw FileNotExistException(recordFile.name)

        if(!mInjector.repo.unlock(recordFileInstance))
            throw FileOperatorException("Lock", "Lock file failed")
    }

    override fun getFilePath(recordFile: RecordFile): String{
        return mInjector.repo.get(recordFile.hashCode())?.file?.absolutePath ?: throw FileNotExistException(recordFile.name)
    }

    override fun deleteFile(recordFile: RecordFile){
        val recordFileInstance = mInjector.repo.get(recordFile.hashCode())
            ?: throw FileNotExistException(recordFile.name)

        if(!mInjector.repo.remove(recordFileInstance.id))
            throw FileOperatorException("Delete", "Delete file failed")
    }

    override fun changType(recordFile: RecordFile, type: FileType){

    }

    override fun lockFile(recordFile: RecordFile){

    }

    override fun unlockFile(recordFile: RecordFile){

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

    /**
     * When provider create a file
     */
    private fun onFileCreated(file : File){
        val recordFile = mInjector.parser.parse(file)

        if(recordFile.info is EventInfo)
            return

    }

    /**
     * When provider write complete
     */
    private fun onFileClosed(file : File){
        val recordFile = mInjector.parser.parse(file)



        recordUpdateListener?.onUpdate()
    }

}