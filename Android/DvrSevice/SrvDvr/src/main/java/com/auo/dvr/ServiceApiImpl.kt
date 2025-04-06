package com.auo.dvr

import com.auo.dvr_core.DvrException
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.OnStateUpdateListener
import com.auo.dvr_core.RecordFile

class ServiceApiImpl : DvrService.IServiceApi() {
    private val mListeners : MutableList<OnRecordUpdateListener> = mutableListOf()

    private val stateListeners : MutableList<OnStateUpdateListener> = mutableListOf()

    private var mState : DvrState = DvrState(false, DvrState.ErrorType.None)
        set(value) {
            field = value
            stateListeners.forEach {
                it.onStateUpdate()
            }
        }

    override var fileManager: DvrService.IFileManager? = null
        set(value) {
            field = value
            val isAvailable = value != null
            mState = DvrState(isAvailable, if(isAvailable) DvrState.ErrorType.None else DvrState.ErrorType.FlashDriveNotAvailable)
            fileManager?.recordUpdateListener = DvrService.IFileManager.RecordUpdateListener {
                mListeners.forEach {
                    it.onUpdate()
                }
            }
        }

    override fun updateState(state: DvrState) {
        mState = state
    }

    override fun getRecordFiles(): List<RecordFile>  = withFileManager { it.recordFiles }
    override fun getState(): DvrState  = mState

    override fun lockFile(recordFile: RecordFile) : Unit = withFileManager { it.lockFile(recordFile) }

    override fun unlockFile(recordFile: RecordFile) : Unit = withFileManager { it.unlockFile(recordFile) }

    override fun deleteFile(recordFile: RecordFile) : Unit = withFileManager { it.deleteFile(recordFile) }

    override fun copyFile(recordFile: RecordFile, destPath: String) : Unit = withFileManager { it.copyFile(recordFile, destPath) }

    override fun registerListener(listener: OnRecordUpdateListener) : Unit = withFileManager {
        if(!mListeners.add(listener)) throw DvrException("DvrService", "Listener already registered") else Unit
    }

    override fun unregisterListener(listener: OnRecordUpdateListener) : Unit = withFileManager {
        if(!mListeners.remove(listener)) throw DvrException("DvrService", "Listener not registered") else Unit
    }

    override fun registerStateListener(listener: OnStateUpdateListener) : Unit = withFileManager {
        if(!stateListeners.add(listener)) throw DvrException("DvrService", "Listener already registered") else Unit
    }

    override fun unregisterStateListener(listener: OnStateUpdateListener) : Unit = withFileManager {
        if(!stateListeners.remove(listener)) throw DvrException("DvrService", "Listener not registered") else Unit
    }
    override fun forceClone() : Unit = withFileManager { it.forceClone() }

    private inline fun<R> withFileManager(block: (DvrService.IFileManager) -> R) : R{
        if(fileManager == null)
            throw DvrException("DvrService", "FileManager is not initialized")
        return block(fileManager!!)
    }
}