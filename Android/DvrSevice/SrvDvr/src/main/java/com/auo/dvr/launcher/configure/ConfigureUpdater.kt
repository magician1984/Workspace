package com.auo.dvr.launcher.configure

import android.os.FileObserver
import com.auo.dvr.launcher.DvrLauncher
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordResolution
import java.io.File

internal class ConfigureUpdater(folder : File) : DvrLauncher.IConfigureUpdater {

    override val configure: DvrConfigure?
        get() = _configure

    private var _configure : DvrConfigure? = null

    private val mConfigureFile : File = File(folder, "configure.cfg")

    private val mFileObserver : FileObserver = object : FileObserver(mConfigureFile.absolutePath, CLOSE_WRITE) {
        override fun onEvent(event: Int, path: String?) {
            if (path == mConfigureFile.name) {
                _configure = readConfig(mConfigureFile)
            }
        }
    }

    init {
        readConfig(mConfigureFile)

        mFileObserver.startWatching()
    }

    protected fun finalize(){
        mFileObserver.stopWatching()
    }

    override fun updateConfigure(configure: DvrConfigure) {
        TODO("Not yet implemented")
    }

    override fun onConfigureUpdate(callback: (DvrConfigure) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onConfigureFileUpdate(eventType: DvrLauncher.IFileManager.EventType, file: File) {
        TODO("Not yet implemented")
    }

    private fun readConfig(file : File) : DvrConfigure? {
        if(!file.exists()) return null

        val lines = file.readLines()
        val configure = DvrConfigure(RecordDuration.entries.first { it.value == lines[0].toLong() }, RecordResolution.entries.first { it.value == lines[1].toInt() })

        return configure
    }
}