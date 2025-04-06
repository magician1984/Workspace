package com.auo.dvr_ui.entity

import java.io.File

interface IUseCase

interface IUseCaseGetListFiles : IUseCase{
    operator fun invoke() : List<RecordFileData>
}

interface IUseCaseRegisterListener : IUseCase{
    operator fun invoke(callback : (List<RecordFileData>) -> Unit)
}

interface IUseCaseLockFile : IUseCase{
    operator fun invoke(record : RecordFileData)
}

interface IUseCaseUnlockFile : IUseCase{
    operator fun invoke(record : RecordFileData)
}

interface IUseCaseDeleteFile : IUseCase{
    operator fun invoke(record : RecordFileData)
}

interface IUseCaseGetCacheFile : IUseCase{
    operator fun invoke(record : RecordFileData) : File
}

interface IUseCaseGetDvrState : IUseCase{
    operator fun invoke() : DvrStateData
}

interface IUseCaseRegisterDvrStateListener : IUseCase{
    operator fun invoke(callback : (DvrStateData) -> Unit)
}

