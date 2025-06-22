package com.auo.dvr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.IDvrService

class DvrService : Service() {
    private lateinit var mServiceApi: IDvrService.Stub

    private var isInitialized : Boolean = false

    override fun onCreate() {
        try{
            cacheDir.listFiles()?.forEach {
                if(it.exists()){
                    it.delete()
                }
            }

            mServiceApi = ServiceApiImpl(this)

            isInitialized = true

            Log.d("DvrService", "onCreate: initialized")
        }catch (e : DvrException){
            Log.e("DvrService", "onCreate: ", e)
        }
    }

    override fun onBind(intent: Intent): IBinder = mServiceApi

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = "Mock Dvr"
        val channelName = "Mock DVR Service Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Mock Service Running")
            .setContentText("This service runs on boot")
            .setSmallIcon(R.drawable.baseline_directions_car_24)
            .build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }
}