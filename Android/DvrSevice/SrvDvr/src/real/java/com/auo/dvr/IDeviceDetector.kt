package com.auo.dvr

import java.io.File

internal interface IDeviceDetector {
    fun interface OnFlashDiskMountStateUpdateListener {
        fun onUpdate(isMounted: Boolean)
    }

    val mountedFolder: File?
    fun setOnFlashDiskMountStateUpdateListener(listener: OnFlashDiskMountStateUpdateListener)
    fun unmount()
}