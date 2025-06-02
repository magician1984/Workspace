package com.auo.dvr_ui.datasource

import android.util.Log
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrEventCallback
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordResolution
import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.usecase.IDataSource

class Datasource(
    private val service: IDvrService
) : IDataSource {
    private var mListeners: MutableList<IDataSource.EventListener> = mutableListOf()

    private var _dvrState: DvrStateData = DvrStateData(DvrState(false, DvrState.ErrorType.None))

    private val _recordGroups: MutableList<RecordGroup> = mutableListOf()

    override val dvrState: DvrStateData
        get() = _dvrState
    override val recordGroups: List<RecordGroup>
        get() = _recordGroups

    private val errorMessages: HashMap<DvrState.ErrorType, String> = hashMapOf()

    init {
        //TODO: String for locale
        errorMessages[DvrState.ErrorType.None] = "Service not start"
        errorMessages[DvrState.ErrorType.FlashDriveNotAvailable] = "Flash drive is not available"
        errorMessages[DvrState.ErrorType.InternalError] = "Internal error"
        errorMessages[DvrState.ErrorType.InRestart] = "In restarting"

        service.registerCallback(object : IDvrEventCallback.Stub() {
            override fun onRecordUpdate(groups: List<RecordGroup>) = updateRecordGroups(groups)

            override fun onStateUpdate(state: DvrState) = updateDvrState(state)

            //Nothing need to do
            override fun onConfigureUpdate(configure: DvrConfigure?) {
            }
        })

        updateDvrState(service.state)

        updateRecordGroups(service.recordGoups)
    }

    override fun registerUpdateListener(listener: IDataSource.EventListener) {
        mListeners.add(listener)
    }

    override fun lockRecords(record: List<RecordGroup>) = withServiceAvailable(onAvailable = {
        service.lockFile(record)
    }, onUnavailable = {
    })

    override fun unlockRecords(record: List<RecordGroup>) = withServiceAvailable(onAvailable = {
        service.unlockFile(record)
    }, onUnavailable = {
    })

    override fun deleteRecords(record: List<RecordGroup>) = withServiceAvailable(onAvailable = {
        service.deleteFile(record)
    }, onUnavailable = {
    })

    override fun unmountStorage(): Unit = withServiceAvailable(onAvailable = {
        service.unmountFlash()
    }, onUnavailable = {})

    override fun getConfigure(): DvrConfigure = withServiceAvailable(
        onAvailable = {
            service.configure!!
        },
        onUnavailable = {
            DvrConfigure(
                duration = RecordDuration.FiveMin,
                resolution = RecordResolution.FHD
            )
        })

    private fun updateDvrState(state: DvrState) {
        val message: String = errorMessages.getOrDefault(state.errorType, "")

        _dvrState = DvrStateData(state, message)
        mListeners.forEach { it.onStateUpdate() }
    }

    private fun updateRecordGroups(groups: List<RecordGroup>) {
        _recordGroups.clear()
        _recordGroups.addAll(groups)
        mListeners.forEach { it.onRecordUpdate() }
    }

    private inline fun <R> withServiceAvailable(
        crossinline onAvailable: (service: IDvrService) -> R,
        onUnavailable: () -> R
    ): R {
        Log.d("Datasource", "withServiceAvailable : ${dvrState.isAvailable}")
        return if (dvrState.isAvailable)
            onAvailable(service)
        else
            onUnavailable()
    }
}