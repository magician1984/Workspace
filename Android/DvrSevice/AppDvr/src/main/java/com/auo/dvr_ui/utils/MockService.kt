package com.auo.dvr_ui.utils

import android.content.Context
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnConfigureUpdateListener
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.OnStateUpdateListener
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordResolution
import com.auo.dvr_core.RecordType
import java.io.File

class MockService(private val context: Context) : IDvrService.Stub() {
    private val mRecordFiles = mutableListOf<RecordFile>()

    private val mListeners = mutableListOf<OnRecordUpdateListener>()

    private val mStateListeners = mutableListOf<OnStateUpdateListener>()

    private val mConfigureListeners = mutableListOf<OnConfigureUpdateListener>()

    private var mState = DvrState(true, DvrState.ErrorType.None)
        set(value) {
            field = value
            mStateListeners.forEach {
                it.onStateUpdate()
            }
        }

    private var isMounted = true
        set(value) {
            field = value
            mState = mState.copy(isAvailable = isMounted, errorType = if(isMounted) DvrState.ErrorType.None else DvrState.ErrorType.FlashDriveNotAvailable)
        }

    private var mConfigure : DvrConfigure = DvrConfigure(RecordDuration.FiveMin, RecordResolution.FHD)

    init {
        //random generate 200 record files. filename is {timestamp}.mp4
        val time = System.currentTimeMillis()
        for (i in 1..200) {
            val randomTime = time - (i * 1000 * 60 * 5)
            val recordFile = RecordFile(
                "${randomTime}.mp4",
                randomTime,
                CamLocation.entries.random(),
                RecordType.entries.random()
            )
            mRecordFiles.add(recordFile)
        }
    }

    override fun getRecordFiles(): MutableList<RecordFile> {
        return if(isMounted) mRecordFiles else mutableListOf()
    }

    override fun getState(): DvrState = mState
    override fun getConfigure(): DvrConfigure = mConfigure

    override fun updataConfigure(configure: DvrConfigure){
        mState = mState.copy(isAvailable = false, errorType = DvrState.ErrorType.InRestart)
        Thread.sleep(2000)
        mState = mState.copy(isAvailable = true, errorType = DvrState.ErrorType.None)
        mConfigure = configure
        mConfigureListeners.forEach { it.onUpdate() }
    }

    override fun lockFile(recordFile: RecordFile) {
        runWithUpdateNotify {
            val index = mRecordFiles.indexOfLast { it.hashCode() == recordFile.hashCode()}
            mRecordFiles[index] = recordFile.copy(type = RecordType.Locked)
        }
    }

    override fun unlockFile(recordFile: RecordFile) {
        runWithUpdateNotify {
            val index = mRecordFiles.indexOfLast { it.hashCode() == recordFile.hashCode()}
            mRecordFiles[index] = recordFile.copy(type = RecordType.Normal)
        }
    }

    override fun deleteFile(recordFile: RecordFile) {
        runWithUpdateNotify {
            mRecordFiles.remove(recordFile)
        }
    }

    override fun copyFile(recordFile: RecordFile, destPath: String) {
        context.assets.open("mock_video.mp4")
            .use { inputStream ->
                File(destPath).outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
    }

    override fun registerListener(listener: OnRecordUpdateListener) : Unit = if(!mListeners.add(listener)) throw Exception("Listener already registered") else Unit
    override fun unregisterListener(listener: OnRecordUpdateListener?) : Unit = if(!mListeners.remove(listener)) throw Exception("Listener not registered") else Unit
    override fun registerStateListener(listener: OnStateUpdateListener) : Unit = if(!mStateListeners.add(listener)) throw Exception("Listener already registered") else Unit
    override fun unregisterStateListener(listener: OnStateUpdateListener) : Unit = if(!mStateListeners.remove(listener)) throw Exception("Listener not registered") else Unit
    override fun registerConfigureListener(listener: OnConfigureUpdateListener?) : Unit = if(!mConfigureListeners.add(listener!!)) throw Exception("Listener already registered") else Unit
    override fun unregisterConfigureListener(listener: OnConfigureUpdateListener?) : Unit = if(!mConfigureListeners.remove(listener!!)) throw Exception("Listener not registered") else Unit

    override fun unmountFlash() {
        Thread.sleep(1000)
        isMounted = false
    }

    override fun forceClone() {

    }

    private inline fun runWithUpdateNotify(crossinline block: () -> Unit){
        block()
        mListeners.forEach { it.onUpdate() }
    }
}