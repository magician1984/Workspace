package com.auo.dvr.detector

import com.auo.dvr.IDeviceDetector
import java.io.File

internal class MockDetector(private val mockFolder : File) : IDeviceDetector {
    override val mountedFolder: File?
        get() = mockFolder

    private var mListener : IDeviceDetector.OnFlashDiskMountStateUpdateListener? = null

    override fun setOnFlashDiskMountStateUpdateListener(listener: IDeviceDetector.OnFlashDiskMountStateUpdateListener) {
        mListener = listener
    }

    override fun unmount() {
        TODO("Not yet implemented")

    }
}