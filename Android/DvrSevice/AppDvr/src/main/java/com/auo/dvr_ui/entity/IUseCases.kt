package com.auo.dvr_ui.entity

import java.io.File

interface IUseCaseRegisterListener{
    operator fun invoke(callback : (List<RecordFileData>) -> Unit)
}

interface IUseCaseLockFile{
    operator fun invoke(record : RecordFileData)
}

interface IUseCaseUnlockFile{
    operator fun invoke(record : RecordFileData)
}

interface IUseCaseDeleteFile{
    operator fun invoke(record : RecordFileData)
}

interface IUseCaseGetCacheFile{
    operator fun invoke(record : RecordFileData) : File
}