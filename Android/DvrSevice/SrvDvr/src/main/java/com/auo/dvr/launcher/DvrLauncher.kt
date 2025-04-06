package com.auo.dvr.launcher

import android.os.FileObserver
import android.util.Log
import com.auo.dvr.DvrServiceState
import com.auo.dvr.DvrService
import java.io.File

internal class DvrLauncher(
    private val sharedPartitionFolder: File,
    private val deviceDetect: IDeviceDetect
) : DvrService.IDvrLauncher {
    companion object {
        private const val EVENT_FILE_EXTENSION = "evt"
        private const val WORKAROUND_FILE_EXTENSION = "h265"
        private const val RECORD_FILE_EXTENSION = "mp4"

        private const val TARGET_FOLDER_NAME = "Dvr_dst"
    }

    internal interface IDeviceDetect {
        val mountedFolder: File?
        fun onFlashDiskMountStateUpdate(callback: (Boolean) -> Unit)
    }

    override val serviceState: DvrServiceState
        get() = _serviceState

    override var onRecordFileUpdateListener: DvrService.IDvrLauncher.OnRecordFileUpdateListener? = null

    override var onServiceStateUpdateListener: DvrService.IDvrLauncher.OnServiceStateUpdateListener? = null

    private var _serviceState: DvrServiceState

    private val workaround: Workaround = Workaround()

    private val mFileObserver: FileObserver =
        object : FileObserver(sharedPartitionFolder.absolutePath, CLOSE_WRITE or CREATE) {
            override fun onEvent(event: Int, path: String?) {
                if (path == null)
                    return

                val eventType: DvrService.IDvrLauncher.EventType =
                    if (event == CLOSE_WRITE) DvrService.IDvrLauncher.EventType.Close else DvrService.IDvrLauncher.EventType.Create
                Log.d("DvrLauncher", "onEvent: $eventType, $path")
                when (path.substringAfterLast('.')) {
                    EVENT_FILE_EXTENSION -> onRecordFileUpdateListener?.onFileUpdate(
                        eventType,
                        DvrService.IDvrLauncher.FileType.Event,
                        File(sharedPartitionFolder, path)
                    )

                    RECORD_FILE_EXTENSION -> onRecordFileUpdateListener?.onFileUpdate(
                        eventType,
                        DvrService.IDvrLauncher.FileType.Record,
                        File(sharedPartitionFolder, path)
                    )

                    WORKAROUND_FILE_EXTENSION -> workaround(
                        eventType,
                        File(sharedPartitionFolder, path)
                    )
                }
            }
        }

    init {
        _serviceState = DvrServiceState(
            if (deviceDetect.mountedFolder != null)
                File(deviceDetect.mountedFolder, TARGET_FOLDER_NAME)
            else
                null
            , deviceDetect.mountedFolder != null
        )

        deviceDetect.onFlashDiskMountStateUpdate(::onFlashDiskMountStateUpdate)

        Log.d(
            "DvrLauncher",
            "sharedPartitionFolder: ${sharedPartitionFolder.absolutePath}, target folder: ${_serviceState.destinationFolder?.absolutePath}"
        )
    }


    override fun start() {
        if(deviceDetect.mountedFolder != null)
            mFileObserver.startWatching()
    }

    override fun stop() {
        if(deviceDetect.mountedFolder != null)
            mFileObserver.stopWatching()
    }

    private fun onFlashDiskMountStateUpdate(isMounted: Boolean) {
        if (isMounted) {
            _serviceState = DvrServiceState(
                File(deviceDetect.mountedFolder, TARGET_FOLDER_NAME),
                true
            )
            mFileObserver.stopWatching()
        } else {
            _serviceState = DvrServiceState(null, false)
            mFileObserver.stopWatching()
        }

        onServiceStateUpdateListener?.onStateUpdate(_serviceState)
    }

    // Workaround: Provider can not generate mp4 file, so we need to convert h265 to mp4
    private fun workaround(eventType: DvrService.IDvrLauncher.EventType, file: File) {
        if (eventType != DvrService.IDvrLauncher.EventType.Create)
            return
        workaround.process(file)
    }
}