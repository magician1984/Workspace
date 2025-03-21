package com.auo.dvr

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.auo.dvr.manager.IFileManager
import com.auo.dvr.manager.filemanager.standard.StandardFileManagerBuilder
import com.auo.dvr_core.IDvrService

class DvrService : Service() {

    private lateinit var mFileManager : IFileManager

    private lateinit var mServiceApi: IDvrService.Stub

    override fun onCreate() {
        mFileManager = StandardFileManagerBuilder().build()
        mServiceApi = ServiceApiImpl(mFileManager)
    }

    override fun onBind(intent: Intent): IBinder = mServiceApi

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }
}