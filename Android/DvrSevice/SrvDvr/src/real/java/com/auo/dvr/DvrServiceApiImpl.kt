package com.auo.dvr

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrEventCallback
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.RecordGroup

internal class DvrServiceApiImpl(private val recordManager: IRecordManager, private val deviceDetector: IDeviceDetector, private val fileObserver: IFileObserver) : IDvrService.Stub() {
    
    private var mDvrState : DvrState = DvrState(false, DvrState.ErrorType.None)
        set(value) {
            field = value
            eventCallback?.onStateUpdate(field)
        }

    private var eventCallback : IDvrEventCallback? = null

    init {
        recordManager.addRecordUpdateListener(::onRecordUpdate)
        deviceDetector.setOnFlashDiskMountStateUpdateListener(::onFlashDiskMountStateUpdate)
        recordManager.onRootFolderChanged(deviceDetector.mountedFolder)
        fileObserver.setOnEventCallback{event, file ->
            if(event == IFileObserver.EventType.Close && file.isDirectory)
                recordManager.onRootFolderChanged(file)
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

    override fun lockFile(recordGroup: List<RecordGroup>): Unit = recordManager.lockRecord(recordGroup)

    override fun unlockFile(recordGroup: List<RecordGroup>): Unit = recordManager.unlockRecord(recordGroup)

    override fun deleteFile(recordGroup: List<RecordGroup>) : Unit = recordManager.deleteRecord(recordGroup)

    override fun registerCallback(callback: IDvrEventCallback?) {
        eventCallback = callback
    }

    override fun unregisterCallback(callback: IDvrEventCallback?) {
        eventCallback = null
    }

    override fun unmountFlash() {
        deviceDetector.unmount()
    }

    private fun onRecordUpdate(){
        eventCallback?.onRecordUpdate(getRecordGoups())
    }
    
    private fun onFlashDiskMountStateUpdate(isMounted : Boolean){
        recordManager.onRootFolderChanged(deviceDetector.mountedFolder)
        mDvrState = DvrState(isMounted, if(isMounted) DvrState.ErrorType.None else DvrState.ErrorType.FlashDriveNotAvailable)
    }
}