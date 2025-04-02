package com.auo.dvr_ui.utils

import android.content.Context
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.IDvrService
import com.auo.dvr_core.OnRecordUpdateListener
import com.auo.dvr_core.RecordFile
import com.auo.dvr_core.RecordType
import java.io.File

class MockService(private val context: Context) : IDvrService.Stub() {
    private val mRecordFiles = mutableListOf<RecordFile>()

    private val mListeners = mutableListOf<OnRecordUpdateListener>()

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
        return mRecordFiles
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

    override fun registerListener(listener: OnRecordUpdateListener?) {
        mListeners.add(listener!!)
    }

    override fun unregisterListener(listener: OnRecordUpdateListener?) {
        mListeners.remove(listener!!)
    }

    override fun forceClone() {

    }

    private inline fun runWithUpdateNotify(crossinline block: () -> Unit){
        block()
        mListeners.forEach { it.onUpdate() }
    }
}