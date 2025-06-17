package com.auo.dvr.recordmanager

import android.content.Context
import android.util.Log
import com.auo.dvr.IRecordManager
import com.auo.dvr.recordmanager.operator.RecordGroupOperator.delete
import com.auo.dvr.recordmanager.operator.RecordGroupOperator.moveTo
import com.auo.dvr.recordmanager.operator.RecordGroupOperator.moveToExternal
import com.auo.dvr.recordmanager.cleaner.DailyRecordCleaner
import com.auo.dvr.recordmanager.convertor.FileConvertor
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import java.io.File

internal class RecordManager private constructor(
    private val convertor: IConvertor,
    private val cleaner: ICleaner
) : IRecordManager {
    companion object {
        private const val TAG = "RecordManager"

        private const val NORMAL_FOLDER_NAME = "Normal"
        private const val LOCKED_FOLDER_NAME = "Locked"
        private const val PROTECTED_FOLDER_NAME = "Protected"
    }



    private var mRootFolder: File? = null
        set(value) {
            field = value?.ensureFolderExist()

            isAvailable = field != null

            if(isAvailable){
                mNormalFolder = File(value, NORMAL_FOLDER_NAME)
                mLockFolder = File(value, LOCKED_FOLDER_NAME)
                mProtectedFolder = File(value, PROTECTED_FOLDER_NAME)
            }
        }

    private var mNormalFolder: File? = null
        set(value){
            field = value.ensureFolderExist()
        }

    private var mLockFolder: File? = null
        set(value){
            field = value.ensureFolderExist()
        }

    private var mProtectedFolder: File? = null
        set(value){
            field = value.ensureFolderExist()
        }

    private val _recordGroups = mutableListOf<RecordGroup>()

    override val recordGroups: List<RecordGroup>
        get() = _recordGroups

    private var mOnRecordUpdateListener: IRecordManager.OnRecordUpdateListener? = null

    private var isAvailable: Boolean = false

    init {
        restoreRecords()

        cleaner.process(_recordGroups)

        cleaner.setOnTriggerListener {
            cleaner.process(_recordGroups)
        }
    }

    override fun onRootFolderChanged(rootFolder: File?) {
        mRootFolder = rootFolder
    }

    override fun onRecordGroupCreated(groupFolder: File) {
        if (groupFolder.isFile)
            return

        val recordGroup: RecordGroup = convertor.parse(groupFolder)

        runWithAvailable(onAvailable = { normalFolder, lockFolder, protectedFolder ->
            when (recordGroup.type) {
                RecordType.Normal -> recordGroup.moveToExternal(normalFolder)
                RecordType.Protected -> recordGroup.moveToExternal(protectedFolder)
                RecordType.Locked -> recordGroup.moveToExternal(lockFolder)
                else->{}
            }

            _recordGroups.add(recordGroup)

            mOnRecordUpdateListener?.onUpdate()
        }, onNotAvailable = {})

    }


    override fun lockRecord(recordGroups: List<RecordGroup>) {
        runWithAvailable(onAvailable = { _, lockFolder, _ ->
            recordGroups.forEach {
                val index = _recordGroups.indexOf(it)
                if (index != -1)
                    _recordGroups[index] = it.moveTo(lockFolder, convertor)
            }
            mOnRecordUpdateListener?.onUpdate()
        })
    }

    override fun unlockRecord(recordGroups: List<RecordGroup>) {
        runWithAvailable(onAvailable = { normalFolder, _, _ ->
            recordGroups.forEach {
                val index = _recordGroups.indexOf(it)
                if (index != -1)
                    _recordGroups[index] = it.moveTo(normalFolder, convertor)
            }
            mOnRecordUpdateListener?.onUpdate()
        })
    }

    override fun deleteRecord(recordGroups: List<RecordGroup>) {
        runWithAvailable(onAvailable = {_, _, _ ->
            recordGroups.forEach {
                val index = _recordGroups.indexOf(it)
                if (index != -1) {
                    it.delete()
                    _recordGroups.removeAt(index)
                }
            }
            mOnRecordUpdateListener?.onUpdate()
        })

    }

    override fun addRecordUpdateListener(listener: IRecordManager.OnRecordUpdateListener) {
        mOnRecordUpdateListener = listener
    }

    private fun restoreRecords() {
        // restore old record
        runWithAvailable( onAvailable = { normalFolder, lockFolder, protectedFolder ->
            normalFolder.listFiles()?.forEach { folder ->
                if (folder.isDirectory) {
                    try {
                        val recordGroup = convertor.parse(folder, RecordType.Normal)
                        _recordGroups.add(recordGroup)
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                    }
                }
            }

            lockFolder.listFiles()?.forEach { folder ->
                if (folder.isDirectory) {
                    try {
                        val recordGroup = convertor.parse(folder, RecordType.Locked)
                        _recordGroups.add(recordGroup)
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                    }
                }
            }

            protectedFolder.listFiles()?.forEach { folder ->
                if (folder.isDirectory) {
                    try {
                        val recordGroup = convertor.parse(folder, RecordType.Protected)
                        _recordGroups.add(recordGroup)
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                    }
                }
            }
        })
    }

    private inline fun runWithAvailable(onAvailable : (normalFolder : File, lockFolder : File, protectedFolder : File) -> Unit, onNotAvailable : () -> Unit = {}){
        if(isAvailable)
            onAvailable(mNormalFolder!!, mLockFolder!!, mProtectedFolder!!)
        else
            onNotAvailable()
    }

    private fun File?.ensureFolderExist() : File?{
        if (this != null && !this.exists()) {
            this.mkdirs()
        }
        return this
    }

    class Builder {
        private lateinit var convertor: IConvertor
        private lateinit var cleaner: ICleaner

        fun build(): RecordManager {
            convertor = FileConvertor()
            cleaner = DailyRecordCleaner()
            return RecordManager(convertor, cleaner)
        }
    }
}