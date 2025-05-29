package com.auo.dvr_ui.usecase

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.entity.IUseCaseDeleteGroups
import com.auo.dvr_ui.entity.IUseCaseGetConfigure
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockGroups
import com.auo.dvr_ui.entity.IUseCaseRegisterDvrStateListener
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseSetConfigure
import com.auo.dvr_ui.entity.IUseCaseUnlockGroups
import com.auo.dvr_ui.entity.IUseCaseUnmountStorage

class UseCaseGetListFiles(private val datasource: IDataSource) : IUseCaseGetListFiles {
    override fun invoke(): List<RecordGroup> {
        return datasource.getAllRecords()
    }
}

class UseCaseRegisterListener(private val datasource: IDataSource) : IUseCaseRegisterListener {
    override fun invoke(callback: (List<RecordGroup>) -> Unit) {
        datasource.registerUpdateListener {
            callback(it)
        }
    }
}

class UseCaseLockGroup(private val datasource: IDataSource) : IUseCaseLockGroups {
    override fun invoke(records: List<RecordGroup>) {
        for(record in records){
            datasource.lockRecord(record)
        }
    }
}

class UseCaseUnlockGroup(private val datasource: IDataSource) : IUseCaseUnlockGroups {
    override fun invoke(records: List<RecordGroup>) {
        for(record in records){
            datasource.unlockRecord(record)
        }
    }
}

class UseCaseDeleteGroup(private val datasource: IDataSource) : IUseCaseDeleteGroups {
    override fun invoke(records: List<RecordGroup>) {
        for(record in records){
            datasource.deleteRecord(record)
        }
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

class UseCaseGetConfigure(private val datasource: IDataSource) : IUseCaseGetConfigure {
    override fun invoke(): DvrConfigure {
        return datasource.getConfigure()
    }
}

class UseCaseSetConfigure(private val datasource: IDataSource) : IUseCaseSetConfigure {
    override fun invoke(configure: DvrConfigure) {
        return datasource.updateConfigure(configure)
    }
}

class UseCaseUnmountStorage(private val datasource: IDataSource) : IUseCaseUnmountStorage {
    override fun invoke() {
        datasource.unmountStorage()
    }
}
