package com.auo.dvr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
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

        enum class EventType{
            Create,
            Close
        }

        interface OnRecordFileUpdateListener{
            fun onFileUpdate(eventType:EventType, type: FileType, file: File)
        }

        val configureFile : DvrConfigure

        var onRecordFileUpdateListener : OnRecordFileUpdateListener?

        fun start()

        fun stop()
    }

    interface IFileManager : IDvrLauncher.OnRecordFileUpdateListener {
        interface Builder{
            fun build() : IFileManager
        }

        fun interface RecordUpdateListener{
            fun onUpdate()
        }

        var recordUpdateListener: RecordUpdateListener?

        val recordFiles : List<RecordFile>

        fun init()
        fun release()

        // Hold to frozen the file operation until release
        fun copyFile(recordFile: RecordFile, destPath: String)

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
        val channelId = "Dvr"
        val channelName = "DVR Service Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, "Dvr")
            .setContentTitle("Service Running")
            .setContentText("This service runs on boot")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mDvrLauncher.stop()

        mFileManager.release()

        super.onDestroy()
    }
}