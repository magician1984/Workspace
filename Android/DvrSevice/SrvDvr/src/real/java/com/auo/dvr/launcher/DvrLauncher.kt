package com.auo.dvr.launcher

import android.content.Context
import android.os.FileObserver
import android.util.Log
import com.auo.dvr.DvrService.IDvrLauncher
import com.auo.dvr.data.UserIntent
import com.auo.dvr.launcher.DvrLauncher.IFileManager.EventType
import com.auo.dvr.launcher.filemanager.FileManagerBuilder
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.RecordFile
import java.io.File

internal class DvrLauncher(
    private val mContext: Context,
    private val mSharedPartitionFolder: File,
    private val mDeviceDetect: IDeviceDetect,
    private val mQnxServerMonitor: IQNXServerMonitor
) : IDvrLauncher {
    companion object {
        private const val EVENT_FILE_EXTENSION = "evt"
        private const val WORKAROUND_FILE_EXTENSION = "h265"
        private const val RECORD_FILE_EXTENSION = "mp4"

        private const val TARGET_FOLDER_NAME = "Dvr_dst"
    }

    interface IFileManager {
        enum class FileType {
            Event,
            Record
        }

        enum class EventType {
            Create,
            Close,
            Delete
        }

        interface Builder {
            fun build(): IFileManager
        }

        fun interface RecordUpdateListener {
            fun onUpdate()
        }

        var recordUpdateListener: RecordUpdateListener?

        val recordFiles: List<RecordFile>

        fun init()
        fun release()

        // Blocking call
        fun copyFile(recordFile: RecordFile, destPath: String)

        fun deleteFile(recordFile: RecordFile)
        fun lockFile(recordFile: RecordFile)
        fun unlockFile(recordFile: RecordFile)
        fun forceClone()
        fun onFileUpdate(eventType: EventType, type: FileType, file: File)
    }

    internal interface IDeviceDetect {
        val mountedFolder: File?
        fun onFlashDiskMountStateUpdate(callback: (Boolean) -> Unit)
        fun unmount()
    }

    internal interface IQNXServerMonitor {
        val configure: DvrConfigure?

        val isActive: Boolean

        fun updateConfigure(configure: DvrConfigure)
        fun registerServerStateListener(listener: (Boolean) -> Unit)
        fun onFileUpdate(eventType: EventType, file: File)
    }

    override var onServiceStateUpdateListener: IDvrLauncher.OnServiceStateUpdateListener? = null
    override var onConfigureUpdateListener: IDvrLauncher.OnConfigureUpdateListener? = null

    private var mServiceState: DvrState = DvrState(false, DvrState.ErrorType.None)
        set(value) {
            field = value
            onServiceStateUpdateListener?.onStateUpdate(value)
        }

    private var mDvrConfigure: DvrConfigure? = null
        set(value) {
            field = value
            if (value == null) return
            onConfigureUpdateListener?.onConfigureUpdate(value)
        }

    private val mWorkaround: Workaround = Workaround()

    private var mFileManager: IFileManager? = null

    private val mFileObserver: FileObserver =
        object : FileObserver(mSharedPartitionFolder.absolutePath, CREATE or CLOSE_WRITE or DELETE) {
            override fun onEvent(event: Int, path: String?) {
                Log.d("DvrLauncher", "onEvent: $event, $path")

                if (path == null)
                    return

                val eventType: IFileManager.EventType = when(event){
                    CREATE -> EventType.Create
                    CLOSE_WRITE -> EventType.Close
                    DELETE -> EventType.Delete
                    else -> return
                }

                Log.d("DvrLauncher", "onEvent: $eventType, $path")
                when (path.substringAfterLast('.')) {
                    EVENT_FILE_EXTENSION -> mFileManager?.onFileUpdate(
                        eventType,
                        IFileManager.FileType.Event,
                        File(mSharedPartitionFolder, path)
                    )

                    RECORD_FILE_EXTENSION -> mFileManager?.onFileUpdate(
                        eventType,
                        IFileManager.FileType.Record,
                        File(mSharedPartitionFolder, path)
                    )

                    WORKAROUND_FILE_EXTENSION -> workaround(
                        eventType,
                        File(mSharedPartitionFolder, path)
                    )

                    else -> mQnxServerMonitor.onFileUpdate(eventType, File(mSharedPartitionFolder, path))
                }
            }
        }

    init {
        mDeviceDetect.onFlashDiskMountStateUpdate(::onFlashDiskMountStateUpdate)

        mQnxServerMonitor.registerServerStateListener(::onQnxServerStateUpdate)

        onFlashDiskMountStateUpdate(mDeviceDetect.mountedFolder != null)

        onQnxServerStateUpdate(mQnxServerMonitor.isActive)

        mFileObserver.startWatching()

        Log.d(
            "DvrLauncher",
            "sharedPartitionFolder: ${mSharedPartitionFolder.absolutePath}, target folder: ${mDeviceDetect.mountedFolder?.absolutePath}"
        )
    }

    override fun <R> handleUserIntent(intent: UserIntent<R>): R {
        Log.d("DvrLauncher", "handleUserIntent: $intent, $mFileManager")
        val result = when (intent) {
            is UserIntent.CopyRecord -> mFileManager?.copyFile(intent.recordFile, intent.destPath)
            is UserIntent.DeleteRecord -> mFileManager?.deleteFile(intent.recordFile)
            UserIntent.GetRecordFiles -> mFileManager?.recordFiles ?: emptyList<RecordFile>()
            is UserIntent.LockRecord -> mFileManager?.lockFile(intent.recordFile)
            is UserIntent.UnlockRecord -> mFileManager?.unlockFile(intent.recordFile)
            UserIntent.GetConfig -> mDvrConfigure
            UserIntent.GetState -> mServiceState
            UserIntent.UnmountStorage -> mDeviceDetect.unmount()
            is UserIntent.UpdateConfig -> updateConfigure(intent.config)
        } ?: Unit

        return result as R
    }


    override fun release() {
        mFileObserver.stopWatching()
    }


    private fun onFlashDiskMountStateUpdate(isMounted: Boolean) {
        mFileManager?.release()

        mFileManager = if (isMounted) {
            FileManagerBuilder().setTargetRoot(
                File(
                    mDeviceDetect.mountedFolder!!,
                    TARGET_FOLDER_NAME
                )
            )
                .setEventCacheRoot(mContext.cacheDir).build()
        } else {
            null
        }

        mFileManager?.init()

        stateUpdate(isFlashMounted = isMounted)
    }

    private fun onQnxServerStateUpdate(isActive: Boolean) {
        mDvrConfigure = mQnxServerMonitor.configure

        stateUpdate(isQnxServerActive = isActive)
    }

    private fun updateConfigure(configure: DvrConfigure) {
        mQnxServerMonitor.updateConfigure(configure)
        mDvrConfigure = configure
    }

    // Workaround: Provider can not generate mp4 file, so we need to convert h265 to mp4
    private fun workaround(eventType: IFileManager.EventType, file: File) {
        if (eventType != IFileManager.EventType.Create)
            return
        mWorkaround.process(file)
    }

    private fun stateUpdate(
        isFlashMounted: Boolean = mDeviceDetect.mountedFolder != null,
        isQnxServerActive: Boolean = mQnxServerMonitor.isActive
    ) {
        var isActive = true
        var errorType: DvrState.ErrorType = DvrState.ErrorType.None

        if (!isFlashMounted) {
            isActive = false
            errorType = DvrState.ErrorType.FlashDriveNotAvailable
        }

        if (!isQnxServerActive) {
            isActive = false
            errorType = DvrState.ErrorType.InRestart
        }

        mDvrConfigure = mQnxServerMonitor.configure

        mServiceState = DvrState(isActive, errorType)

        Log.d("DvrLauncher", "$isFlashMounted, $isQnxServerActive, $mServiceState")
    }
}