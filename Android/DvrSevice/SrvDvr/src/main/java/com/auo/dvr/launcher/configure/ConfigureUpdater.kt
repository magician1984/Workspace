package com.auo.dvr.launcher.configure

import android.content.Context
import com.auo.dvr.launcher.DvrLauncher
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordResolution
import java.io.File
import java.util.Locale
import androidx.core.content.edit

internal class ConfigureUpdater(private val folder: File, private val context : Context) : DvrLauncher.IConfigureUpdater {

    companion object {
        private const val PARAM_FORMAT = "%s:%s"
        private const val KEY_DURATION = "duration"
        private const val KEY_RESOLUTION = "resolution"
    }

    override val configure: DvrConfigure
        get() = _configure


    private var _configure: DvrConfigure = DvrConfigure(RecordDuration.FiveMin, RecordResolution.FHD)

    private val mConfigureFile: File = File(folder, "configure.lock")

    private var isEnabled = false

    init {
        readConfigFromSharedPerformance(_configure)
        updateConfigure(_configure)
    }

    override fun updateConfigure(configure: DvrConfigure) {
        _configure = configure
        writeConfigFromSharedPerformance(configure)
        if(isEnabled)
            enable()
    }

    override fun enable() {
        if(mConfigureFile.exists())
            mConfigureFile.delete()

        val file: File = File(folder, "configure.tmp")

        if(file.exists())
            file.delete()

        file.createNewFile()

        file.writeText(
            """
            ${String.format(Locale.TAIWAN, PARAM_FORMAT, KEY_DURATION, configure.duration.value)})}
            ${
                String.format(
                    Locale.TAIWAN,
                    PARAM_FORMAT,
                    KEY_RESOLUTION,
                    configure.resolution.value
                )
            }
        """.trimIndent()
        )

        file.renameTo(mConfigureFile)

        isEnabled = true
    }

    override fun disable() {
        if(mConfigureFile.exists())
            mConfigureFile.delete()

        isEnabled = false
    }


    override fun release() {
        disable()
        writeConfigFromSharedPerformance(_configure)
    }

    private fun readConfigFromSharedPerformance(configure: DvrConfigure){
        val sharedPreferences = context.getSharedPreferences("DvrConfigure", Context.MODE_PRIVATE)
        val duration = sharedPreferences.getLong(KEY_DURATION, configure.duration.value)
        val resolution = sharedPreferences.getInt(KEY_RESOLUTION, configure.resolution.value)
        _configure = DvrConfigure(RecordDuration.entries.find { it.value == duration }!!, RecordResolution.entries.find { it.value == resolution }!!)
    }

    private fun writeConfigFromSharedPerformance(configure: DvrConfigure){
        val sharedPreferences = context.getSharedPreferences("DvrConfigure", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putLong(KEY_DURATION, configure.duration.value)
            putInt(KEY_RESOLUTION, configure.resolution.value)
        }
    }
}