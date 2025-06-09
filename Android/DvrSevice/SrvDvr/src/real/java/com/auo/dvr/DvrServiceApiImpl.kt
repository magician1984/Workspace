package com.auo.dvr

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrEventCallback
import com.auo.dvr_core.RecordGroup

internal class DvrServiceApiImpl() : IDvrServiceApi() {

    private var mRecordManager : IRecordManager? = null
        set(value) {
            field = value
            if(field != null){
                field!!.addRecordUpdateListener(::onRecordUpdate)
            }
            mDvrState = DvrState(isAvailable = field != null, errorType = if(field == null) DvrState.ErrorType.FlashDriveNotAvailable else DvrState.ErrorType.None)
        }

    private var mDvrState : DvrState = DvrState(false, DvrState.ErrorType.None)
        set(value) {
            field = value
            eventCallback?.onStateUpdate(field)
        }

    private var eventCallback : IDvrEventCallback? = null

    override fun setRecordManager(recordManager: IRecordManager?) {
        mRecordManager = recordManager
    }

    override fun getRecordGoups(): List<RecordGroup> = mRecordManager?.recordGroups ?: emptyList()

    override fun getState(): DvrState = mDvrState

    override fun getConfigure(): DvrConfigure {
        TODO("Not yet implemented")
    }

    override fun updataConfigure(configure: DvrConfigure?) {
        TODO("Not yet implemented")
    }

    override fun lockFile(recordGroup: List<RecordGroup>): Unit = mRecordManager?.lockRecord(recordGroup) ?: Unit

    override fun unlockFile(recordGroup: List<RecordGroup>): Unit = mRecordManager?.unlockRecord(recordGroup) ?: Unit

    override fun deleteFile(recordGroup: List<RecordGroup>) : Unit = mRecordManager?.deleteRecord(recordGroup) ?: Unit

    override fun registerCallback(callback: IDvrEventCallback?) {
        eventCallback = callback
    }

    override fun unregisterCallback(callback: IDvrEventCallback?) {
        eventCallback = null
    }

    override fun unmountFlash() {
        TODO("Not yet implemented")
    }

    private fun onRecordUpdate(){
        eventCallback?.onRecordUpdate(getRecordGoups())
    }
}