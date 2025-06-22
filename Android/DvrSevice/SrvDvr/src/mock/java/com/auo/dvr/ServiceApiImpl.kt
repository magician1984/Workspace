package com.auo.dvr

import android.content.Context
import android.net.Uri
import android.util.Log
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ServiceApiImpl(private val context: Context) : IDvrService.Stub() {
    companion object {
        private const val MOCK_FOLDER = "mock_record"
        private const val MOCK_VIDEO_EXTENSION = ".ts"
        private const val MOCK_THUMBNAIL_EXTENSION = ".nv12"
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
        val targetFolder = File(context.filesDir, MOCK_FOLDER).apply {
            if (!exists())
                mkdirs()
        }
        // Copy mock folder to Cache folder from assets
        val mockFiles = context.assets.list(MOCK_FOLDER)

        mockFiles?.forEach { mockFile ->
            val targetFile = File(targetFolder, mockFile)

            try{
                context.assets.open("$MOCK_FOLDER/$mockFile").use { inputStream ->
                    FileOutputStream(targetFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }catch (e : IOException){
                Log.e("ServiceApiImpl", "Create mock file failed ", e)
            }
        }

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
                            Uri.fromFile(File(targetFolder, "$location$MOCK_VIDEO_EXTENSION")),
                            Uri.fromFile(File(targetFolder, "$location$MOCK_THUMBNAIL_EXTENSION"))
                        )
                    )
                }
            }

            val recordGroup = RecordGroup(timestamp, mockRecordList, RecordType.Normal, uri = Uri.EMPTY)
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