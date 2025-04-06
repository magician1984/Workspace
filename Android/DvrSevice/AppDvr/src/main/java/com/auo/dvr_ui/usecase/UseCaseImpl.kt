package com.auo.dvr_ui.usecase

import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterDvrStateListener
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseUnlockFile
import com.auo.dvr_ui.entity.RecordFileData
import java.io.File

class UseCaseGetListFiles(private val datasource: IDataSource) : IUseCaseGetListFiles {
    override fun invoke(): List<RecordFileData> {
        return datasource.getAllRecords()
    }
}

class UseCaseRegisterListener(private val datasource: IDataSource) : IUseCaseRegisterListener {
    override fun invoke(callback: (List<RecordFileData>) -> Unit) {
        datasource.registerUpdateListener {
            callback(it)
        }
    }
}

class UseCaseLockFile(private val datasource: IDataSource) : IUseCaseLockFile {
    override fun invoke(record: RecordFileData) {
        datasource.lockRecord(record)
    }
}

class UseCaseUnlockFile(private val datasource: IDataSource) : IUseCaseUnlockFile {
    override fun invoke(record: RecordFileData) {
        datasource.unlockRecord(record)
    }
}

class UseCaseDeleteFile(private val datasource: IDataSource) : IUseCaseDeleteFile {
    override fun invoke(record: RecordFileData) {
        datasource.deleteRecord(record)
    }
}

class UseCaseGetCacheFile(private val datasource: IDataSource) : IUseCaseGetCacheFile {
    override fun invoke(record: RecordFileData): File {
        return datasource.getCacheFile(record)
    }
}

class UseCaseGetDvrState(private val datasource: IDataSource) : IUseCaseGetDvrState {
    override fun invoke(): DvrStateData {
        return datasource.dvrState
    }
}

class UseCaseRegisterDvrStateListener(private val datasource: IDataSource) :
    IUseCaseRegisterDvrStateListener {
    override fun invoke(callback: (DvrStateData) -> Unit) {
        datasource.registerDvrStateListener{
            callback(it)
        }
    }

}
