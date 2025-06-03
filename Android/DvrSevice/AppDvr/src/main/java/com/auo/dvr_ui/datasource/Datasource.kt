package com.auo.dvr_ui.datasource

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity.BIND_AUTO_CREATE
import com.auo.dvr.DvrService
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrEventCallback
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordResolution
import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.usecase.IDataSource

class Datasource(context: Context) : IDataSource {
    private val mRecordUpdateListeners: MutableList<IDataSource.RecordUpdateListener> = mutableListOf()

    private val mDvrStateUpdateListeners: MutableList<IDataSource.DveStateUpdateListener> = mutableListOf()

    private var _dvrState: DvrStateData = DvrStateData(DvrState(false, DvrState.ErrorType.None))

    private val _recordGroups: MutableList<RecordGroup> = mutableListOf()

    private var mService: IDvrService? = null

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

        // Bind service
        bindService(context)
    }

    override fun registerRecordUpdateListener(listener: IDataSource.RecordUpdateListener) {
        mRecordUpdateListeners.add(listener)
    }

    override fun registerDveStateUpdateListener(listener: IDataSource.DveStateUpdateListener) {
        mDvrStateUpdateListeners.add(listener)
    }

    override fun lockRecords(record: List<RecordGroup>) = withServiceAvailable(onAvailable = {
        service ->  service.lockFile(record)
    }, onUnavailable = {
    })

    override fun unlockRecords(record: List<RecordGroup>) = withServiceAvailable(onAvailable = {
        service ->  service.unlockFile(record)
    }, onUnavailable = {
    })

    override fun deleteRecords(record: List<RecordGroup>) = withServiceAvailable(onAvailable = {
        service ->  service.deleteFile(record)
    }, onUnavailable = {
    })

    override fun unmountStorage(): Unit = withServiceAvailable(onAvailable = {
        service ->  service.unmountFlash()
    }, onUnavailable = {})

    override fun getConfigure(): DvrConfigure = withServiceAvailable(
        onAvailable = {
            service ->  service.configure!!
        },
        onUnavailable = {
            DvrConfigure(
                duration = RecordDuration.FiveMin,
                resolution = RecordResolution.FHD
            )
        })

    private fun updateDvrState(state: DvrState) {
        Log.d("Datasource", "updateDvrState: $state")

        val message: String = errorMessages.getOrDefault(state.errorType, "")

        _dvrState = DvrStateData(state, message)
        mDvrStateUpdateListeners.forEach{it.onUpdate()}
    }

    private fun updateRecordGroups(groups: List<RecordGroup>) {
        _recordGroups.clear()
        _recordGroups.addAll(groups)
        mRecordUpdateListeners.forEach { it.onUpdate() }
    }

    private fun bindService(context: Context) {
        val intent = Intent(context, DvrService::class.java)
        context.bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                service?.let {
                    Log.d("Datasource", "onServiceConnected")
                    initService(IDvrService.Stub.asInterface(it))
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                TODO("Not yet implemented")
            }
        }, BIND_AUTO_CREATE)
    }

    private fun initService(service: IDvrService){
        mService = service.also {
            Log.d("DataSource", "Register callback")
            it.registerCallback(object : IDvrEventCallback.Stub() {
                override fun onRecordUpdate(groups: List<RecordGroup>) = updateRecordGroups(groups)

                override fun onStateUpdate(state: DvrState) = updateDvrState(state)

                //Nothing need to do
                override fun onConfigureUpdate(configure: DvrConfigure?) {
                }
            })

            Log.d("DataSource", "Get state")
            updateDvrState(it.state)
            updateRecordGroups(it.recordGoups)
        }
    }

    private inline fun <R> withServiceAvailable(
        crossinline onAvailable: (service: IDvrService) -> R,
        onUnavailable: () -> R
    ): R {
        Log.d("Datasource", "withServiceAvailable : ${dvrState.isAvailable}")
        return if (dvrState.isAvailable && mService != null)
            onAvailable(mService!!)
        else
            onUnavailable()
    }
}