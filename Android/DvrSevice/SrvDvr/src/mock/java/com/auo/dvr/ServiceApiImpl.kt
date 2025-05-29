package com.auo.dvr

import android.content.Context
import android.net.Uri
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnConfigureUpdateListener
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.OnStateUpdateListener
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordResolution
import com.auo.dvr_core.RecordType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.random.Random

class ServiceApiImpl(private val context: Context) : DvrService.IServiceApi() {
    private val mRecordGroups = mutableListOf<RecordGroup>()

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
        val mockVideoPath = File(context.cacheDir, "mock.mp4")

        context.assets.open("mock_video.mp4").use { input ->
            FileOutputStream(mockVideoPath).use { output ->
                input.copyTo(output)
            }
        }

        val mockUri : Uri = Uri.fromFile(mockVideoPath)

        //random generate 200 record grout with four record file, timestamp is millisec, time scale is min for goup, second for file . filename is {timestamp}.mp4
        val recordCount = 200
        val time = System.currentTimeMillis()
        for (i in 1..200) {
            // time + i * 1 min
            val min = time + i * 60 * 1000
            val fileList = buildList<RecordFile>(CamLocation.entries.size){
                // Create random second between 0 and 59
                CamLocation.entries.forEach {
                    val timeWithSec = min + Random.nextInt(0, 60) * 1000
                    add(RecordFile("${timeWithSec}.mp4", timeWithSec, it, mockUri))
                }
            }
            mRecordGroups.add(RecordGroup(min, fileList, RecordType.Normal))
        }
    }

    override fun updateState(state: DvrState) {

    }

    override fun getRecordGoups(): MutableList<RecordGroup> {
        return if(isMounted) mRecordGroups else mutableListOf()
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

    override fun lockFile(recordGroup: RecordGroup) {
        runWithUpdateNotify {
            val index = mRecordGroups.indexOfFirst { it.timestamp == recordGroup.timestamp }
            mRecordGroups[index] = recordGroup.copy(type = RecordType.Locked)
        }
    }

    override fun unlockFile(recordGroup: RecordGroup) {
        runWithUpdateNotify {
            val index = mRecordGroups.indexOfFirst { it.timestamp == recordGroup.timestamp }
            mRecordGroups[index] = recordGroup.copy(type = RecordType.Normal)
        }
    }

    override fun deleteFile(recordGroup: RecordGroup?) {
        runWithUpdateNotify {
            mRecordGroups.remove(recordGroup)
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


    private inline fun runWithUpdateNotify(crossinline block: () -> Unit){
        block()
        mListeners.forEach { it.onUpdate() }
    }
}