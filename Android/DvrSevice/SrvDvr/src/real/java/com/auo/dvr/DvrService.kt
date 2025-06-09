package com.auo.dvr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.auo.dvr.data.UserIntent
import com.auo.dvr.launcher.DvrLauncher
import com.auo.dvr.detector.UsbDetector
import com.auo.dvr.launcher.monitor.QNXServerMonitor
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrService
import java.io.File

class DvrService : Service() {
    internal interface IDvrLauncher {

        fun interface OnServiceStateUpdateListener {
            fun onStateUpdate(state: DvrState)
        }

        fun interface OnConfigureUpdateListener {
            fun onConfigureUpdate(configure: DvrConfigure)
        }

        fun interface OnRecordUpdateListener {
            fun onRecordUpdate()
        }

        var onServiceStateUpdateListener: OnServiceStateUpdateListener?

        var onConfigureUpdateListener: OnConfigureUpdateListener?

        var onRecordUpdateListener: OnRecordUpdateListener?

        fun <R> handleUserIntent(intent: UserIntent<R>): R

        fun release()
    }



    private lateinit var mServiceApi: IDvrService.Stub

    private lateinit var mDvrLauncher: IDvrLauncher

    private var isInitialized: Boolean = false

    override fun onCreate() {
        try {
            Log.d("DvrService", "onCreate:")
            val sourceFolder = File("/mnt/nfs", "Dvr_src")

            if (!sourceFolder.exists())
                sourceFolder.mkdirs()

            mServiceApi = DvrServiceApiImpl()

            isInitialized = true

            Log.d("DvrService", "onCreate: initialized")
        } catch (e: DvrException) {
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
            .setSmallIcon(R.drawable.baseline_directions_car_24)
            .build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mDvrLauncher.release()

        super.onDestroy()
    }
}