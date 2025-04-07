package com.auo.dvr.launcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.io.File

class UsbDetector(private val mContext: Context) : DvrLauncher.IDeviceDetect, BroadcastReceiver() {

    private var _mountedFolder: File? = null

    override val mountedFolder: File?
        get() = _mountedFolder

    private var mCallback: ((Boolean) -> Unit)? = null

    init {
        //Register USB and unmount broadcast receiver
        val filter : IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addAction(Intent.ACTION_MEDIA_UNMOUNTED)
        }
        mContext.registerReceiver(this, filter)
    }

    override fun onFlashDiskMountStateUpdate(callback: (Boolean) -> Unit) {
        mCallback = callback
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }
}