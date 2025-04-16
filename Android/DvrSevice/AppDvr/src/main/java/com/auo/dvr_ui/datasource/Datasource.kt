package com.auo.dvr_ui.datasource

import android.content.Context
import android.util.Log
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.OnStateUpdateListener
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordResolution
import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.usecase.IDataSource
import java.io.File

class Datasource(
    val context: Context,
    private val service: IDvrService,
    private val cacheFolder: File
) : IDataSource {
    private var mListeners: MutableList<IDataSource.EventListener> = mutableListOf()

    private var mStateListeners: MutableList<IDataSource.DvrStateListener> = mutableListOf()

    private var _dvrState: DvrStateData = DvrStateData(DvrState(true, DvrState.ErrorType.None))

    override val dvrState: DvrStateData
        get() = _dvrState

    private val errorMessages: HashMap<DvrState.ErrorType, String> = hashMapOf()

    init {
        //TODO: String for locale
        errorMessages[DvrState.ErrorType.None] = "Service not start"
        errorMessages[DvrState.ErrorType.FlashDriveNotAvailable] = "Flash drive is not available"
        errorMessages[DvrState.ErrorType.InternalError] = "Internal error"
        errorMessages[DvrState.ErrorType.InRestart] = "In restarting"

        Log.d("Datasource", "register listener")
        service.registerListener(object : OnRecordUpdateListener.Stub() {
            override fun onUpdate() {
                val records = getAllRecords()
                mListeners.forEach { it.onUpdate(records) }
            }
        })

        Log.d("Datasource", "register state listener")
        service.registerStateListener(object : OnStateUpdateListener.Stub() {
            override fun onStateUpdate() {
                updateDvrState()
            }
        })

        updateDvrState()
    }

    override fun getAllRecords(): List<RecordFileData> = withServiceAvailable(onAvailable = {
        service.recordFiles.map {
            val cacheFile = File(cacheFolder, "${it.hashCode()}.mp4")
            RecordFileData(it, if (cacheFile.exists()) cacheFile else null)
        }
    }, onUnavailable = { emptyList() }
    )

    override fun registerUpdateListener(listener: IDataSource.EventListener) {
        mListeners.add(listener)
    }

    override fun registerDvrStateListener(listener: IDataSource.DvrStateListener) {
        mStateListeners.add(listener)
    }

    override fun lockRecord(record: RecordFileData): Unit = withServiceAvailable(onAvailable = {
        service.lockFile(record.dto)
    }, onUnavailable = {})

    override fun unlockRecord(record: RecordFileData): Unit = withServiceAvailable(onAvailable = {
        service.unlockFile(record.dto)
    }, onUnavailable = {})

    override fun deleteRecord(record: RecordFileData): Unit = withServiceAvailable(onAvailable = {
        service.deleteFile(record.dto)
    }, onUnavailable = {})

    override fun getCacheFile(record: RecordFileData): File {
        val cacheFile = File(cacheFolder, "${record.id}.mp4")
        withServiceAvailable(onAvailable = {
            if (!cacheFile.exists())
                service.copyFile(record.dto, cacheFile.absolutePath)
        }, onUnavailable = {})
        return cacheFile
    }

    override fun unmountStorage(): Unit = withServiceAvailable(onAvailable = {
        service.unmountFlash()
    }, onUnavailable = {})

    override fun updateConfigure(configure: DvrConfigure): Unit =
        withServiceAvailable(onAvailable = {
            service.updataConfigure(configure)
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

    private fun updateDvrState() {
        Log.d("Datasource", "updateDvrState:")
        val state = service.state

        Log.d("Datasource", "updateDvrState: state: $state")
        val message: String = errorMessages.getOrDefault(state.errorType, "")

        Log.d("Datasource", "updateDvrState: message: $message")
        _dvrState = DvrStateData(state, message)

        Log.d("Datasource", "updateDvrState: listeners: ${mStateListeners.size}")
        mStateListeners.forEach { it.onUpdate(dvrState) }

        if (state.isAvailable) {
            val records = getAllRecords()
            mListeners.forEach { it.onUpdate(records) }
        }
    }

    private inline fun <R> withServiceAvailable(
        crossinline onAvailable: (service: IDvrService) -> R,
        onUnavailable: () -> R
    ): R {
        return if (dvrState.isAvailable)
            onAvailable(service)
        else
            onUnavailable()
    }
}