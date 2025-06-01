package com.auo.dvr_ui.entity

import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType

interface IUseCase

interface IUseCaseGetRecordGroups : IUseCase{
    operator fun invoke(set: Set<RecordType>) : List<RecordGroup>
}

interface IUseCaseRegisterListener : IUseCase{
    operator fun invoke(onRecordGroupUpdate : () -> Unit, onDvrStateUpdate : () -> Unit)
}

interface IUseCaseLockGroups : IUseCase{
    operator fun invoke(records : List<RecordGroup>)
}

interface IUseCaseUnlockGroups : IUseCase{
    operator fun invoke(records : List<RecordGroup>)
}

interface IUseCaseDeleteGroups : IUseCase{
    operator fun invoke(records : List<RecordGroup>)
}

interface IUseCaseGetDvrState : IUseCase{
    operator fun invoke() : DvrStateData
}

interface IUseCaseUnmountStorage : IUseCase{
    operator fun invoke()
}
