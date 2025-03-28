package com.auo.dvr_ui.datasource

import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.usecase.IDataSource
import java.io.File

class Datasource(private val service : IDvrService,private val cacheFolder : File) : IDataSource {
    private var mListeners : MutableList<IDataSource.EventListener> = mutableListOf()

    init {
        service.registerListener(object : OnRecordUpdateListener.Stub() {
            override fun onUpdate() {
                val records = service.recordFiles.map { RecordFileData(it) }
                mListeners.forEach { it.onUpdate(records) }
            }
        })
    }

    override fun registerUpdateListener(listener: IDataSource.EventListener) {
        mListeners.add(listener)
    }

    override fun lockRecord(record: RecordFileData) {
        service.lockFile(record.dto)
    }

    override fun unlockRecord(record: RecordFileData) {
        service.unlockFile(record.dto)
    }

    override fun deleteRecord(record: RecordFileData) {
        service.deleteFile(record.dto)
    }

    override fun getCacheFile(record: RecordFileData): File {
        val cacheFile = File(cacheFolder, "${record.id}.mp4")
        if(!cacheFile.exists())
            service.copyFile(record.dto, cacheFile.absolutePath)
        return cacheFile
    }
}