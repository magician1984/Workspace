package com.auo.dvr

import android.util.Log
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrEventCallback
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.RecordGroup

internal class DvrServiceApiImpl(
    private val recordManager: IRecordManager,
    private val deviceDetector: IDeviceDetector,
    private val fileObserver: IFileObserver,
    private val remoteConnector: IRemoteConnector
) : IDvrService.Stub() {

    private var mIsFlashMounted: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                onStateUpdate(isFlashMounted = field)
            }
        }

    private var mIsRemoteAvailable: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                onStateUpdate(isRemoteAvailable = field)
            }
        }

    private var mDvrState: DvrState = DvrState(false, DvrState.ErrorType.None)
        set(value) {
            field = value
            eventCallback?.onStateUpdate(field)
        }

    private var eventCallback: IDvrEventCallback? = null

    init {
        recordManager.addRecordUpdateListener(::onRecordUpdate)
        deviceDetector.setOnFlashDiskMountStateUpdateListener(::onFlashDiskMountStateUpdate)
        onFlashDiskMountStateUpdate(deviceDetector.mountedFolder != null)
        remoteConnector.addOnStateUpdateListener(::onRemoteAvailableUpdate)
        onRemoteAvailableUpdate(remoteConnector.isAvailable)
        fileObserver.setOnEventCallback { event, file ->
            if (event == IFileObserver.EventType.Close && file.isDirectory)
                recordManager.onRecordGroupCreated(file)
        }
    }

    override fun getRecordGoups(): List<RecordGroup> = recordManager.recordGroups

    override fun getState(): DvrState = mDvrState

    override fun getConfigure(): DvrConfigure {
        TODO("Not yet implemented")
    }

    override fun updataConfigure(configure: DvrConfigure?) {
        TODO("Not yet implemented")
    }

    override fun lockFile(recordGroup: List<RecordGroup>): Unit =
        recordManager.lockRecord(recordGroup)

    override fun unlockFile(recordGroup: List<RecordGroup>): Unit =
        recordManager.unlockRecord(recordGroup)

    override fun deleteFile(recordGroup: List<RecordGroup>): Unit =
        recordManager.deleteRecord(recordGroup)

    override fun registerCallback(callback: IDvrEventCallback?) {
        eventCallback = callback
    }

    override fun unregisterCallback(callback: IDvrEventCallback?) {
        eventCallback = null
    }

    override fun unmountFlash() {
        deviceDetector.unmount()
    }

    private fun onRecordUpdate() {
        eventCallback?.onRecordUpdate(getRecordGoups())
    }

    private fun onFlashDiskMountStateUpdate(isMounted: Boolean) {
        Log.d("DvrServiceApiImpl", "onFlashDiskMountStateUpdate: $isMounted")
        recordManager.onRootFolderChanged(deviceDetector.mountedFolder)
        mIsFlashMounted = isMounted
    }

    private fun onRemoteAvailableUpdate(isAvailable: Boolean) {
        Log.d("DvrServiceApiImpl", "onRemoteAvailableUpdate: $isAvailable")
        mIsRemoteAvailable = isAvailable
    }

    private fun onStateUpdate(
        isFlashMounted: Boolean = mIsFlashMounted,
        isRemoteAvailable: Boolean = mIsRemoteAvailable
    ) {
        Log.d("DvrServiceApiImpl", "onStateUpdate: $isFlashMounted, $isRemoteAvailable")
        val isAvailable = isFlashMounted && isRemoteAvailable
        mDvrState = DvrState(
            isAvailable,
            if (isAvailable) DvrState.ErrorType.None else DvrState.ErrorType.FlashDriveNotAvailable
        )
    }
}