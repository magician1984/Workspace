package com.auo.performancetester.domain.usecase

import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.FileAllocateMode
import com.auo.performancetester.domain.entity.FileSize
import com.auo.performancetester.domain.entity.IData

interface IUseCase

interface IUseCaseStartTest:IUseCase{
    operator fun invoke(method: CloneMethod, allocateMode: FileAllocateMode, fileSize: FileSize, count: Int, forceWrite: Boolean)
}

interface IUseCaseListenEvents:IUseCase{
    operator fun invoke(callback : (IData) -> Unit)
}

interface IUseCaseExit:IUseCase{
    operator fun invoke()
}

interface IUseCaseInitialize:IUseCase{
    operator fun invoke()
}
