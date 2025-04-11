package com.auo.dvr.launcher.detector

import android.content.Context
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Log
import com.auo.dvr.launcher.DvrLauncher
import java.io.File
import java.util.concurrent.Executors

class UsbDetector(mContext: Context) : DvrLauncher.IDeviceDetect {
    companion object{
        private const val USB_MOUNT_FOLDER = "/mnt/media_rw"
    }

    private var _mountedFolder: File? = null

    override val mountedFolder: File?
        get() = _mountedFolder

    private var mCallback: ((Boolean) -> Unit)? = null

    private val mStorageManager : StorageManager = mContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager

    init {
        val uuid : String? = mStorageManager.storageVolumes.find { it.isRemovable }?.uuid

        if(uuid != null)
            _mountedFolder = File(USB_MOUNT_FOLDER, uuid)

        Log.d("UsbDetector", "mountedFolder: ${_mountedFolder?.absolutePath}")

        mStorageManager.registerStorageVolumeCallback(Executors.newSingleThreadExecutor(),  object : StorageManager.StorageVolumeCallback(){
            override fun onStateChanged(volume: StorageVolume) {
                super.onStateChanged(volume)
                Log.d("UsbDetector", "storage state changed: ${volume.uuid}, removable : ${volume.isRemovable}, state : ${volume.state}")
                if(!volume.isRemovable)
                    return

                val mUUID : String = volume.uuid ?: return

                if(volume.state == Environment.MEDIA_MOUNTED && _mountedFolder == null){
                    _mountedFolder = File(USB_MOUNT_FOLDER, mUUID)
                    mCallback?.invoke(true)
                }else if(volume.state != Environment.MEDIA_MOUNTED && _mountedFolder?.name == uuid){
                    _mountedFolder = null
                    mCallback?.invoke(false)
                }
            }
        })
    }

    override fun onFlashDiskMountStateUpdate(callback: (Boolean) -> Unit) {
        mCallback = callback
    }

    override fun unmount() {
        val folder : File = _mountedFolder ?: return
        StorageManager::class.java.getMethod("unmount", String::class.java).invoke(mStorageManager, folder.name)
    }
}