package com.auo.dvr

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.auo.dvr.filemanager.FileManagerBuilder
import com.auo.dvr.launcher.DvrLauncher
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.RecordFile
import java.io.File

class DvrService : Service() {
    interface IDvrLauncher{
        enum class FileType{
            Event,
            Record
        }

        interface OnRecordFileUpdateListener{
            fun onFileClosed(type: FileType, file: File)
            fun onFileCreated(type: FileType, file: File)
        }

        val configureFile : DvrConfigure

        var onRecordFileUpdateListener : OnRecordFileUpdateListener?

        fun start()

        fun stop()
    }

    interface IFileManager : IDvrLauncher.OnRecordFileUpdateListener {
        interface Builder<T : IFileManager>{
            fun build() : T
        }

        fun interface RecordUpdateListener{
            fun onUpdate()
        }

        var recordUpdateListener: RecordUpdateListener?

        val recordFiles : List<RecordFile>

        fun init()
        fun release()

        // Hold to frozen the file operation until release
        fun holdFile(recordFile: RecordFile)
        fun releaseFile(recordFile: RecordFile)
        fun getFilePath(recordFile: RecordFile) : String


        fun deleteFile(recordFile: RecordFile)
        fun lockFile(recordFile: RecordFile)
        fun unlockFile(recordFile: RecordFile)
        fun forceClone()
    }

    private lateinit var mFileManager : IFileManager

    private lateinit var mServiceApi: IDvrService.Stub

    private lateinit var mDvrLauncher : IDvrLauncher

    private var isInitialized : Boolean = false

    override fun onCreate() {
        try{
            //Workaround: Shared partition folder is not ready yet, use Downloads folder instead
            val sourceFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Dvr_src")

            mDvrLauncher = DvrLauncher(sourceFolder)

            mFileManager = FileManagerBuilder()
                .setTargetRoot(mDvrLauncher.configureFile.destinationFolder)
                .build()

            mServiceApi = ServiceApiImpl(mFileManager)

            mFileManager.init()

            mDvrLauncher.start()

            isInitialized = true
        }catch (e : DvrException){
            Log.e("DvrService", "onCreate: ", e)
        }
    }

    override fun onBind(intent: Intent): IBinder = mServiceApi

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mDvrLauncher.stop()

        mFileManager.release()

        super.onDestroy()
    }
}