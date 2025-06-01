package com.auo.dvr_ui.usecase

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.entity.DvrStateData
import com.auo.dvr_ui.entity.IUseCaseDeleteGroups
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetRecordGroups
import com.auo.dvr_ui.entity.IUseCaseLockGroups
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseUnlockGroups
import com.auo.dvr_ui.entity.IUseCaseUnmountStorage

class UseCaseGetListFiles(private val datasource: IDataSource) : IUseCaseGetRecordGroups {
    override fun invoke(set: Set<RecordType>): List<RecordGroup> {
        return datasource.recordGroups.filter { it.type in set }
    }
}

class UseCaseRegisterListener(private val datasource: IDataSource) : IUseCaseRegisterListener {
    override fun invoke(
        onRecordGroupUpdate: () -> Unit,
        onDvrStateUpdate: () -> Unit
    ) {
        datasource.registerUpdateListener(object : IDataSource.EventListener {
            override fun onRecordUpdate() = onRecordGroupUpdate()

            override fun onStateUpdate() = onDvrStateUpdate()
        })
    }
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
