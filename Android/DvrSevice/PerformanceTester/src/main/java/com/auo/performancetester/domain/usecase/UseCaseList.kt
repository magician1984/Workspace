package com.auo.performancetester.domain.usecase

import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.FileAllocateMode
import com.auo.performancetester.domain.entity.FileSize
import com.auo.performancetester.domain.entity.IData
import com.auo.performancetester.domain.entity.ThreadSize

interface IUseCase

interface IUseCaseStartTest : IUseCase {
    operator fun invoke(
        method: CloneMethod = CloneMethod.FileChannel,
        allocateMode: FileAllocateMode = FileAllocateMode.PreAllocate,
        fileSize: FileSize = FileSize.Large,
        count: Int = 20,
        forceWrite: Boolean = true,
        threadCount: ThreadSize = ThreadSize.One
    )
}

interface IUseCaseListenEvents : IUseCase {
    operator fun invoke(callback: (IData) -> Unit)
}

interface IUseCaseExit : IUseCase {
    operator fun invoke()
}

interface IUseCaseInitialize : IUseCase {
    operator fun invoke()
}
