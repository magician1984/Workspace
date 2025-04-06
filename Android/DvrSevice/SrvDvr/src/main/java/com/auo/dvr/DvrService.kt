package com.auo.dvr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.auo.dvr.filemanager.FileManagerBuilder
import com.auo.dvr.launcher.DvrLauncher
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.DvrState
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

        interface OnServiceStateUpdateListener{
            fun onStateUpdate(state: DvrServiceState)
        }

        val serviceState : DvrServiceState

        var onRecordFileUpdateListener : OnRecordFileUpdateListener?

        var onServiceStateUpdateListener : OnServiceStateUpdateListener?

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

        // Blocking call
        fun copyFile(recordFile: RecordFile, destPath: String)

        fun deleteFile(recordFile: RecordFile)
        fun lockFile(recordFile: RecordFile)
        fun unlockFile(recordFile: RecordFile)
        fun forceClone()
    }

    abstract class IServiceApi : IDvrService.Stub(){
        abstract var fileManager: IFileManager?
        abstract fun updateState(state: DvrState)
    }

    private var mFileManager : IFileManager? = null

    private lateinit var mServiceApi: IServiceApi

    private lateinit var mDvrLauncher : IDvrLauncher

    private var isInitialized : Boolean = false

    override fun onCreate() {
        try{
            //Workaround: Shared partition folder is not ready yet, use Downloads folder instead
            val sourceFolder = File(getExternalFilesDir(null), "Dvr_src")

            if(!sourceFolder.exists())
                sourceFolder.mkdirs()

            mDvrLauncher = DvrLauncher(sourceFolder, object : DvrLauncher.IDeviceDetect{
                override val mountedFolder: File?
                    get() = getExternalFilesDir(null)

                override fun onFlashDiskMountStateUpdate(callback: (Boolean) -> Unit) {

                }
            })

            if(mDvrLauncher.serviceState.available){
                mFileManager = FileManagerBuilder()
                    .setTargetRoot(mDvrLauncher.serviceState.destinationFolder!!)
                    .build()

                mFileManager!!.init()
            }

            mDvrLauncher.onRecordFileUpdateListener = mFileManager

            mServiceApi = ServiceApiImpl()

            mServiceApi.fileManager = mFileManager

            mDvrLauncher.onServiceStateUpdateListener = object : IDvrLauncher.OnServiceStateUpdateListener{
                override fun onStateUpdate(state: DvrServiceState) {
                    if(state.available){
                        mFileManager = FileManagerBuilder()
                            .setTargetRoot(state.destinationFolder!!)
                            .build()
                        mFileManager!!.init()
                    }else{
                        mFileManager?.release()
                        mFileManager = null
                    }
                    mServiceApi.fileManager = mFileManager
                }
            }

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

        mFileManager?.release()

        super.onDestroy()
    }
}