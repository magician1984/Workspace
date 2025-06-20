package com.auo.dvr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.auo.dvr.detector.MockDetector
import com.auo.dvr.detector.UsbDetector
import com.auo.dvr.observer.PollingFileObserver
import com.auo.dvr.recordmanager.RecordManager
import com.auo.dvr.remote.QNXServiceConnector
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrEventCallback
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.RecordGroup
import java.io.File

class DvrService : Service() {
    companion object {
        private const val TAG = "DvrService"

        private val SRC_FOLDER =  File("/mnt/nfs", "Dvr_src").apply {
            if(!exists())
                mkdirs()
        }

//        private val SRC_FOLDER = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//            "Dvr_src"
//        ).apply {
//            if (!exists())
//                mkdirs()
//        }

        private const val POLLING_TIME_MILLISECONDS = 3000L
    }

    private lateinit var mServiceApi: IDvrService.Stub

    private lateinit var mDeviceDetector: IDeviceDetector

    private lateinit var mRecordManager: IRecordManager

    private lateinit var mFileObserver: IFileObserver

    private lateinit var mRemoteConnector: IRemoteConnector

    private var isInitialized: Boolean = false

    override fun onCreate() {
        init()
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
            .setSmallIcon(R.drawable.baseline_directions_car_24)
            .build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    private fun init() {
        try {
            Log.d(TAG, "Start to init")

            mRemoteConnector = QNXServiceConnector(rootFolder = SRC_FOLDER)

            mFileObserver =
                PollingFileObserver(mFolder = SRC_FOLDER, mInterval = POLLING_TIME_MILLISECONDS)

            mRecordManager = RecordManager.Builder().build()

//            mDeviceDetector = UsbDetector(mContext = this)

            mDeviceDetector = MockDetector(File(cacheDir, "mock_target").apply {
                if(!this.exists())
                    this.mkdirs()
            })

            mServiceApi = DvrServiceApiImpl(
                recordManager = mRecordManager,
                deviceDetector = mDeviceDetector,
                fileObserver = mFileObserver,
                remoteConnector = mRemoteConnector
            )

            mFileObserver.start()

            isInitialized = true

            Log.d(TAG, "init done!")
        } catch (e: DvrException) {
            Log.e(TAG, "init failed: ", e)
            mServiceApi = object : IDvrService.Stub() {
                private val exception = DvrException("Api", "Initialize failed")

                override fun getRecordGoups(): List<RecordGroup> = emptyList()
                override fun getState(): DvrState =
                    DvrState(false, DvrState.ErrorType.InternalError)

                override fun getConfigure(): DvrConfigure = throwException()
                override fun updataConfigure(configure: DvrConfigure?): Unit = throwException()
                override fun lockFile(recordGroup: List<RecordGroup>?): Unit = throwException()
                override fun unlockFile(recordGroup: List<RecordGroup>?): Unit = throwException()
                override fun deleteFile(recordGroup: List<RecordGroup>?): Unit = throwException()
                override fun registerCallback(callback: IDvrEventCallback?): Unit = throwException()
                override fun unregisterCallback(callback: IDvrEventCallback?): Unit =
                    throwException()

                override fun unmountFlash(): Unit = throwException()
                private inline fun <reified T> throwException(): T {
                    throw exception
                }
            }
        }
    }

    private fun release() {
        mFileObserver.stop()
    }
}