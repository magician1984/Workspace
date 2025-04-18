package com.auo.dvr.launcher.monitor

import android.os.FileObserver
import android.util.Log
import com.auo.dvr.launcher.DvrLauncher
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordResolution
import java.io.File
import java.util.Locale

internal class QNXServerMonitor(folder: File) : DvrLauncher.IQNXServerMonitor {

    companion object {
        private const val REQ_FILE = "req.cfg"
        private const val LOCK_FILE = "configure.lock"
        private const val PARAM_FORMAT = "%s:%s"
        private const val KEY_DURATION = "duration"
        private const val KEY_RESOLUTION = "resolution"
    }

    override val configure: DvrConfigure?
        get() = _configure

    override val isActive: Boolean
        get() = _configure != null

    private var _configure: DvrConfigure? = null
        set(value) {
            field = value
            mCallback?.invoke(isActive)
        }

    private val mLockFile: File = File(folder, LOCK_FILE)

    private val mReqFile: File = File(folder, REQ_FILE)

    private var mCallback: ((isActive : Boolean) -> Unit)? = null

    init {
        _configure = readLockConfigure()
    }

    override fun updateConfigure(configure: DvrConfigure) {
        writeConfigureReq(configure)
    }

    override fun registerServerStateListener(listener: (isActive : Boolean) -> Unit) {
        mCallback = listener
    }

    override fun onFileUpdate(eventType: DvrLauncher.IFileManager.EventType, file: File) {
        Log.d("QNXServerMonitor", "onFileUpdate: $eventType, ${file.absolutePath}")
        if(file.absolutePath == mLockFile.absolutePath){
            _configure = readLockConfigure()
        }
    }

    private fun writeConfigureReq(configure: DvrConfigure){
        if(mReqFile.exists())
            mReqFile.delete()

        File(mReqFile.parent, mReqFile.name + ".tmp").apply {
            if(exists())
                delete()

            writeText(
                """
                ${String.format(Locale.getDefault(), PARAM_FORMAT, KEY_DURATION, configure.duration.value)}
                ${String.format(Locale.getDefault(), PARAM_FORMAT, KEY_RESOLUTION, configure.resolution.value)}
                """.trimIndent())
            renameTo(mReqFile)
        }
    }

    private fun readLockConfigure() : DvrConfigure?{
        if(!mLockFile.exists())
            return null

        val lines = mLockFile.readLines()

        val duration = lines.find { it.startsWith(KEY_DURATION) }?.substringAfter(":")?.trim()?.toLong() ?: return null

        val resolution = lines.find { it.startsWith(KEY_RESOLUTION) }?.substringAfter(":")?.trim()?.toInt() ?: return null

        return DvrConfigure(RecordDuration.entries.find { it.value == duration }!!, RecordResolution.entries.find { it.value == resolution }!!)
    }
}