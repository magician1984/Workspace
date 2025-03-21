package com.auo.dvr

import com.auo.dvr.manager.filemanager.IFileManager
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordFileTypeDef

class ServiceApiImpl(private val mFileManager: IFileManager) : IDvrService.Stub() {
    private val mListeners : MutableList<OnRecordUpdateListener> = mutableListOf()

    init {
        mFileManager.recordUpdateListener = IFileManager.RecordUpdateListener {
            mListeners.forEach {
                it.onUpdate()
            }
        }
    }

    override fun getRecordFiles(): List<RecordFile> = mFileManager.recordFiles

    override fun lockFile(recordFile: RecordFile) : Unit = mFileManager.lockFile(recordFile)

    override fun unlockFile(recordFile: RecordFile) : Unit = mFileManager.unlockFile(recordFile)

    override fun protectFile(recordFile: RecordFile) : Unit = mFileManager.changType(recordFile, RecordFileTypeDef.Protect)

    override fun unprotectFile(recordFile: RecordFile) : Unit = mFileManager.changType(recordFile, RecordFileTypeDef.Normal)

    override fun deleteFile(recordFile: RecordFile) : Unit = mFileManager.deleteFile(recordFile)

    override fun getRecodFilePath(recordFile: RecordFile): String = mFileManager.getFilePath(recordFile)

    override fun registerOnRecordUpdateListener(listener: OnRecordUpdateListener) : Unit = if(!mListeners.add(listener)) throw DvrException("ServiceApiImpl", "register listener failed") else Unit
}