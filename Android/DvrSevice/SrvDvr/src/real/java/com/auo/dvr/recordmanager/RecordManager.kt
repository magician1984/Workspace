package com.auo.dvr.recordmanager

import android.util.Log
import com.auo.dvr.IRecordManager
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import java.io.File

internal class RecordManager(
    roodFolder : File,
    private val convertor: IConvertor,
    private val cleaner: ICleaner
) : IRecordManager {
    companion object{
        private const val TAG = "RecordManager"
    }

    private val mNormalFolder = File(roodFolder, "Normal").apply { if(!exists()) mkdirs() }

    private val mLockFolder = File(roodFolder, "Locked").apply { if(!exists()) mkdirs() }

    private val mProtectedFolder = File(roodFolder, "Protected").apply { if(!exists()) mkdirs() }

    private val _recordGroups = mutableListOf<RecordGroup>()

    override val recordGroups: List<RecordGroup>
        get() = _recordGroups

    private var mOnRecordUpdateListener : IRecordManager.OnRecordUpdateListener? = null

    init {
        // restore old record
        mNormalFolder.listFiles()?.forEach { folder ->
            if (folder.isDirectory) {
                try {
                    val recordGroup = convertor.parse(folder, RecordType.Normal)
                    _recordGroups.add(recordGroup)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
        }

        mLockFolder.listFiles()?.forEach { folder ->
            if (folder.isDirectory) {
                try {
                    val recordGroup = convertor.parse(folder, RecordType.Locked)
                    _recordGroups.add(recordGroup)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
        }

        mProtectedFolder.listFiles()?.forEach { folder ->
            if (folder.isDirectory) {
                try {
                    val recordGroup = convertor.parse(folder, RecordType.Protected)
                    _recordGroups.add(recordGroup)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
        }

        cleaner.process(_recordGroups)

        cleaner.setOnTriggerListener{
            cleaner.process(_recordGroups)
        }
    }

    override fun onRecordGroupCreated(groupFolder: File) {
        if(groupFolder.isFile)
            return
        val recordGroup : RecordGroup = convertor.parse(groupFolder)

        when(recordGroup.type){
            RecordType.Normal -> recordGroup.moveToExternal(mNormalFolder)
            RecordType.Protected -> recordGroup.moveToExternal(mProtectedFolder)
            else->{}
        }

        _recordGroups.add(recordGroup)

        mOnRecordUpdateListener?.onUpdate()
    }


    override fun lockRecord(recordGroups: List<RecordGroup>) {
        recordGroups.forEach{
            val index = _recordGroups.indexOf(it)
            if(index != -1)
                _recordGroups[index] = it.moveTo(mLockFolder)
        }
        mOnRecordUpdateListener?.onUpdate()
    }

    override fun unlockRecord(recordGroups: List<RecordGroup>) {
        recordGroups.forEach{
            val index = _recordGroups.indexOf(it)
            if(index != -1)
                _recordGroups[index] = it.moveTo(mNormalFolder)
        }
        mOnRecordUpdateListener?.onUpdate()
    }

    override fun deleteRecord(recordGroups: List<RecordGroup>) {
        recordGroups.forEach{
            val index = _recordGroups.indexOf(it)
            if(index != -1){
                it.delete()
                _recordGroups.removeAt(index)
            }
        }
        mOnRecordUpdateListener?.onUpdate()
    }

    override fun addRecordUpdateListener(listener: IRecordManager.OnRecordUpdateListener) {
        mOnRecordUpdateListener = listener
    }
}