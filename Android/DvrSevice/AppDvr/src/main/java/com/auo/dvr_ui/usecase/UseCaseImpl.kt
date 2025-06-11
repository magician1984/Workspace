package com.auo.dvr_ui.usecase

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.entity.IUseCaseDeleteGroups
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetRecordGroups
import com.auo.dvr_ui.entity.IUseCaseLockGroups
import com.auo.dvr_ui.entity.IUseCaseRegisterDvrStateUpdateListener
import com.auo.dvr_ui.entity.IUseCaseRegisterRecordUpdateListener
import com.auo.dvr_ui.entity.IUseCaseUnlockGroups
import com.auo.dvr_ui.entity.IUseCaseUnmountStorage

class UseCaseGetListFiles(private val datasource: IDataSource) : IUseCaseGetRecordGroups {
    override fun invoke(set: Set<RecordType>): List<RecordGroup> {
        val list : List<RecordGroup> = datasource.recordGroups
        val filterList : List<RecordGroup> = list.filter { it.type in set }
        return filterList
    }
}

class UseCaseRegisterRecordUpdateListener(private val datasource: IDataSource) : IUseCaseRegisterRecordUpdateListener {
    override fun invoke(onUpdate: () -> Unit) = datasource.registerRecordUpdateListener(onUpdate)
}

class UseCaseRegisterDvrStateListener(private val datasource: IDataSource) :
    IUseCaseRegisterDvrStateUpdateListener {
    override fun invoke(onUpdate: () -> Unit) = datasource.registerDveStateUpdateListener(onUpdate)

}

class UseCaseLockGroup(private val datasource: IDataSource) : IUseCaseLockGroups {
    override fun invoke(records: List<RecordGroup>) = datasource.lockRecords(records)
}

class UseCaseUnlockGroup(private val datasource: IDataSource) : IUseCaseUnlockGroups {
    override fun invoke(records: List<RecordGroup>) = datasource.unlockRecords(records)
}

class UseCaseDeleteGroup(private val datasource: IDataSource) : IUseCaseDeleteGroups {
    override fun invoke(records: List<RecordGroup>) = datasource.deleteRecords(records)
}

class UseCaseGetDvrState(private val datasource: IDataSource) : IUseCaseGetDvrState {
    override fun invoke(): DvrStateData {
        return datasource.dvrState
    }
}

class UseCaseUnmountStorage(private val datasource: IDataSource) : IUseCaseUnmountStorage {
    override fun invoke() {
        datasource.unmountStorage()
    }
}
