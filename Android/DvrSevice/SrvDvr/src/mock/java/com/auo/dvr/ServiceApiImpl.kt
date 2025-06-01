package com.auo.dvr

import android.content.Context
import android.net.Uri
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrEventCallback
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordResolution
import com.auo.dvr_core.RecordType

class ServiceApiImpl(private val context: Context) : IDvrService.Stub() {
    companion object {
        private const val MOCK_FILE = "mock_video.ts"
        private const val MOCK_THUMBNAIL = "mock_video.png"
        private const val MOCK_RECORD_COUNT = 50
    }

    private val mRecordList: MutableList<RecordGroup> = mutableListOf()

    private var mState: DvrState = DvrState(true, DvrState.ErrorType.None)
        set(value) {
            field = value
            mDvrEventCallback.forEach { it.onStateUpdate(field) }
        }

    private var mConfigure: DvrConfigure = DvrConfigure(RecordDuration.OneMin, RecordResolution.FHD)
        set(value) {
            field = value
            mDvrEventCallback.forEach { it.onConfigureUpdate(field) }
        }

    private var mDvrEventCallback: MutableList<IDvrEventCallback> = mutableListOf()

    init {
        // Copy mock video to Cache folder from assets
        val inputStream = context.assets.open(MOCK_FILE)
        val outputStream = context.openFileOutput(MOCK_FILE, Context.MODE_PRIVATE)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        // Copy mock thumbnail to Cache folder from assets
        val thumbnailInputStream = context.assets.open(MOCK_THUMBNAIL)
        val thumbnailOutputStream = context.openFileOutput(MOCK_THUMBNAIL, Context.MODE_PRIVATE)
        thumbnailInputStream.copyTo(thumbnailOutputStream)
        thumbnailInputStream.close()

        // Generate mock data. the time duration is 1 min
        val durationMs = 60L * 1000L
        val startTime = System.currentTimeMillis() - durationMs * MOCK_RECORD_COUNT.toLong()

        for (i in 0 until MOCK_RECORD_COUNT) {
            val timestamp = startTime + i * durationMs
            val mockRecordList: List<RecordFile> = buildList {
                CamLocation.entries.forEach { location ->
                    this.add(
                        RecordFile(
                            location.name,
                            timestamp,
                            location,
                            Uri.fromFile(context.getFileStreamPath(MOCK_FILE)),
                            Uri.fromFile(context.getFileStreamPath(MOCK_THUMBNAIL))
                        )
                    )
                }
            }
            val recordGroup = RecordGroup(timestamp, mockRecordList, RecordType.Normal)
            mRecordList.add(recordGroup)
        }
    }

    override fun getRecordGoups(): List<RecordGroup> = mRecordList

    override fun getState(): DvrState = mState

    override fun getConfigure(): DvrConfigure = mConfigure

    override fun updataConfigure(configure: DvrConfigure) {
        mState = DvrState(false, DvrState.ErrorType.None)
        mConfigure = configure
        Thread.sleep(1000)
        mState = DvrState(true, DvrState.ErrorType.None)
    }

    override fun lockFile(recordGroups: List<RecordGroup>) = updateRecord(recordGroups) { index ->
        mRecordList[index] = mRecordList[index].copy(type = RecordType.Locked)
    }

    override fun unlockFile(recordGroups: List<RecordGroup>) = updateRecord(recordGroups) { index ->
        mRecordList[index] = mRecordList[index].copy(type = RecordType.Normal)
    }

    override fun deleteFile(recordGroups: List<RecordGroup>) = updateRecord(recordGroups) { index ->
        mRecordList.removeAt(index)
    }

    override fun registerCallback(callback: IDvrEventCallback) {
        if (!mDvrEventCallback.contains(callback))
            mDvrEventCallback.add(callback)
    }

    override fun unregisterCallback(callback: IDvrEventCallback) {
        if (mDvrEventCallback.contains(callback))
            mDvrEventCallback.remove(callback)
    }


    override fun unmountFlash() {
        mState = DvrState(false, DvrState.ErrorType.FlashDriveNotAvailable)
    }

    private inline fun updateRecord(recordGroups: List<RecordGroup>, action: (Int) -> Unit) {
        for (group in recordGroups) {
            val index = mRecordList.indexOf(group)

            if (index != -1) {
                action(index)
            }
        }

        mDvrEventCallback.forEach { it.onRecordUpdate(mRecordList) }
    }
}