package com.auo.dvr_ui.entity

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordGroup

interface IUseCase

interface IUseCaseGetListFiles : IUseCase{
    operator fun invoke() : List<RecordGroup>
}

interface IUseCaseRegisterListener : IUseCase{
    operator fun invoke(callback : (List<RecordGroup>) -> Unit)
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

interface IUseCaseRegisterDvrStateListener : IUseCase{
    operator fun invoke(callback : (DvrStateData) -> Unit)
}

interface IUseCaseGetConfigure : IUseCase{
    operator fun invoke() : DvrConfigure
}

interface IUseCaseSetConfigure : IUseCase{
    operator fun invoke(configure : DvrConfigure)
}

interface IUseCaseUnmountStorage : IUseCase{
    operator fun invoke()
}
