package com.auo.dvr

import com.auo.dvr.data.UserIntent
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.DvrException
import com.auo.dvr_core.DvrState
import com.auo.dvr_core.OnConfigureUpdateListener
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.OnStateUpdateListener
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordResolution

class ServiceApiImpl(dvrLauncher: DvrService.IDvrLauncher) : DvrService.IServiceApi(dvrLauncher) {
    private val mRecordUpdateListeners: MutableList<OnRecordUpdateListener> = mutableListOf()

    private val mStateUpdateListeners: MutableList<OnStateUpdateListener> = mutableListOf()

    private val mConfigUpdateListener: MutableList<OnConfigureUpdateListener> = mutableListOf()

    private var mState: DvrState = mDvrLauncher.handleUserIntent(UserIntent.GetState)
        set(value) {
            field = value
            mStateUpdateListeners.forEach {
                it.onStateUpdate()
            }
        }

    private var mConfigure: DvrConfigure = DvrConfigure(duration = RecordDuration.FiveMin, resolution = RecordResolution.FHD)
        set(value) {
            field = value
            mConfigUpdateListener.forEach {
                it.onUpdate()
            }
        }

    init {
        mDvrLauncher.onServiceStateUpdateListener =
            DvrService.IDvrLauncher.OnServiceStateUpdateListener { state ->
                mState = state
            }

        mDvrLauncher.onConfigureUpdateListener = DvrService.IDvrLauncher.OnConfigureUpdateListener { configure ->
            mConfigure = configure
        }

        mDvrLauncher.onRecordUpdateListener = DvrService.IDvrLauncher.OnRecordUpdateListener {
            mRecordUpdateListeners.forEach {
                it.onUpdate()
            }
        }
    }

    override fun updateState(state: DvrState) {
        mState = state
    }

    override fun getRecordFiles(): List<RecordFile> =
        mDvrLauncher.handleUserIntent(UserIntent.GetRecordFiles)

    override fun getState(): DvrState = mState
    override fun getConfigure(): DvrConfigure = mConfigure

    override fun updataConfigure(configure: DvrConfigure): Unit =
        mDvrLauncher.handleUserIntent(UserIntent.UpdateConfig(configure))

    override fun lockFile(recordFile: RecordFile): Unit =
        mDvrLauncher.handleUserIntent(UserIntent.LockRecord(recordFile))

    override fun unlockFile(recordFile: RecordFile): Unit =
        mDvrLauncher.handleUserIntent(UserIntent.UnlockRecord(recordFile))

    override fun deleteFile(recordFile: RecordFile): Unit =
        mDvrLauncher.handleUserIntent(UserIntent.DeleteRecord(recordFile))

    override fun copyFile(recordFile: RecordFile, destPath: String): Unit =
        mDvrLauncher.handleUserIntent(UserIntent.CopyRecord(recordFile, destPath))

    override fun registerListener(listener: OnRecordUpdateListener): Unit =
        if (!mRecordUpdateListeners.add(listener)) throw DvrException(
            "DvrService",
            "Listener already registered"
        ) else Unit

    override fun unregisterListener(listener: OnRecordUpdateListener): Unit =
        if (!mRecordUpdateListeners.remove(listener)) throw DvrException(
            "DvrService",
            "Listener not registered"
        ) else Unit

    override fun registerStateListener(listener: OnStateUpdateListener): Unit =
        if (!mStateUpdateListeners.add(listener)) throw DvrException(
            "DvrService",
            "Listener already registered"
        ) else Unit

    override fun unregisterStateListener(listener: OnStateUpdateListener): Unit =
        if (!mStateUpdateListeners.remove(listener)) throw DvrException(
            "DvrService",
            "Listener not registered"
        ) else Unit

    override fun registerConfigureListener(listener: OnConfigureUpdateListener): Unit =
        if (!mConfigUpdateListener.add(listener)) throw DvrException(
            "DvrService",
            "Listener already registered"
        ) else Unit

    override fun unregisterConfigureListener(listener: OnConfigureUpdateListener): Unit =
        if (!mConfigUpdateListener.remove(listener)) throw DvrException(
            "DvrService",
            "Listener not registered"
        ) else Unit


    override fun unmountFlash() : Unit = mDvrLauncher.handleUserIntent(UserIntent.UnmountStorage)

    override fun forceClone() {
        TODO("Not yet implemented")
    }
}