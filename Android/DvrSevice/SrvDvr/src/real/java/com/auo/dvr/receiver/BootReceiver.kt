package com.auo.dvr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.auo.dvr.DvrService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Start Dvr Service when boot
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "onReceive: boot completed")
            val serviceIntent = Intent(context, DvrService::class.java)
            context?.startForegroundService(serviceIntent)
        }
    }
}