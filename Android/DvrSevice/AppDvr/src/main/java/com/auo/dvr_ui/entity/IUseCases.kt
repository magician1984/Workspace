package com.auo.dvr_ui.entity

import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordGroup
import java.io.File

interface IUseCase

interface IUseCaseGetListFiles : IUseCase{
    operator fun invoke() : List<RecordGroup>
}

interface IUseCaseRegisterListener : IUseCase{
    operator fun invoke(callback : (List<RecordGroup>) -> Unit)
}

@Deprecated("Use IUseCaseLockGroups instead")
interface IUseCaseLockFile : IUseCase{
    operator fun invoke(record : RecordGroup)
}

interface IUseCaseLockGroups : IUseCase{
    operator fun invoke(records : List<RecordGroup>)
}

@Deprecated("Use IUseCaseUnlockGroups instead")
interface IUseCaseUnlockFile : IUseCase{
    operator fun invoke(record : RecordGroup)
}

interface IUseCaseUnlockGroups : IUseCase{
    operator fun invoke(records : List<RecordGroup>)
}

@Deprecated("Use IUseCaseDeleteGroups instead")
interface IUseCaseDeleteFile : IUseCase{
    operator fun invoke(record : RecordGroup)
}

interface IUseCaseDeleteGroups : IUseCase{
    operator fun invoke(records : List<RecordGroup>)
}

@Deprecated("Not used")
interface IUseCaseGetCacheFile : IUseCase{
    operator fun invoke(record : RecordGroup) : File
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
