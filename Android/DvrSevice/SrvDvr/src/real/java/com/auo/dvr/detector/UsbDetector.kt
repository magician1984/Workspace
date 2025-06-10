package com.auo.dvr.detector

import android.content.Context
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Log
import com.auo.dvr.IDeviceDetector
import java.io.File
import java.util.concurrent.Executors

internal class UsbDetector(mContext: Context) : IDeviceDetector {
    private data class MountInfo(val uuid: String, val id: String, val path: String)

    private var mMountInfo: MountInfo? = null
        set(value) {
            field = value
            mListener?.onUpdate(value != null)
        }

    override val mountedFolder: File?
        get() {
            mMountInfo?.run {
                return File(path)
            } ?: return null
        }

    private var mListener : IDeviceDetector.OnFlashDiskMountStateUpdateListener? = null

    override fun setOnFlashDiskMountStateUpdateListener(listener: IDeviceDetector.OnFlashDiskMountStateUpdateListener) {
        mListener = listener
    }

    private val mStorageManager: StorageManager =
        mContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager

    init {
        val volume: StorageVolume? =
            mStorageManager.storageVolumes.find { it.isRemovable && it.state == Environment.MEDIA_MOUNTED }

        if (volume != null)
            mMountInfo = parseVolume(volume)

        Log.d("UsbDetector", "mountedFolder: ${mMountInfo?.path}")

        mStorageManager.registerStorageVolumeCallback(
            Executors.newSingleThreadExecutor(),
            object : StorageManager.StorageVolumeCallback() {
                override fun onStateChanged(volume: StorageVolume) {
                    super.onStateChanged(volume)

                    if (volume.uuid == null)
                        return

                    if (volume.state == Environment.MEDIA_MOUNTED && volume.isRemovable && mMountInfo == null) {
                        mMountInfo = parseVolume(volume)
                    }
                    if ((volume.state == Environment.MEDIA_EJECTING || volume.state == Environment.MEDIA_UNMOUNTED) && volume.uuid == mMountInfo?.uuid) {
                        mMountInfo = null
                    }

                }
            })
    }

    override fun unmount() {
        val id: String = mMountInfo?.id ?: return
        StorageManager::class.java.getMethod("unmount", String::class.java)
            .invoke(mStorageManager, id)
    }

    private fun parseVolume(volume: StorageVolume): MountInfo? {
        val uuid: String = volume.uuid ?: return null
        val id: String =
            volume::class.java.getMethod("getId").invoke(volume) as? String ?: return null
        val path: String =
            volume::class.java.getMethod("getPath").invoke(volume) as? String ?: return null
        Log.d("UsbDetector", "parseVolume:$uuid, $id, $path")
        return MountInfo(uuid, id, path)
    }
}