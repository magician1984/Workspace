package com.auo.dvr

import com.auo.dvr_core.DvrException
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.RecordFile

class ServiceApiImpl(private val mFileManager: DvrService.IFileManager) : IDvrService.Stub() {
    private val mListeners : MutableList<OnRecordUpdateListener> = mutableListOf()

    init {
        mFileManager.recordUpdateListener = DvrService.IFileManager.RecordUpdateListener {
            mListeners.forEach {
                it.onUpdate()
            }
        }
    }

    override fun getRecordFiles(): List<RecordFile>  = mFileManager.recordFiles

    override fun lockFile(recordFile: RecordFile) : Unit = mFileManager.lockFile(recordFile)

    override fun unlockFile(recordFile: RecordFile) : Unit = mFileManager.unlockFile(recordFile)

    override fun deleteFile(recordFile: RecordFile) : Unit = mFileManager.deleteFile(recordFile)

    override fun copyFile(recordFile: RecordFile, destPath: String) = mFileManager.copyFile(recordFile, destPath)

    override fun registerListener(listener: OnRecordUpdateListener) : Unit = if(!mListeners.add(listener)) throw DvrException("DvrService", "Listener already registered") else Unit

    override fun unregisterListener(listener: OnRecordUpdateListener) : Unit = if(!mListeners.remove(listener)) throw DvrException("DvrService", "Listener not registered") else Unit

    override fun forceClone() : Unit = mFileManager.forceClone()

}