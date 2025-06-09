package com.auo.performancetester.domain.usecase.impl

import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.FileAllocateMode
import com.auo.performancetester.domain.entity.FileSize
import com.auo.performancetester.domain.entity.TestCaseConfigure
import com.auo.performancetester.domain.entity.ThreadSize
import com.auo.performancetester.domain.usecase.IUseCaseStartTest

class UseCaseStartTest(private val testSource: IDataSource) : IUseCaseStartTest {
    override fun invoke(method: CloneMethod, allocateMode: FileAllocateMode, fileSize: FileSize, count: Int, forceWrite: Boolean, threadCount : ThreadSize) {
        testSource.startTest(TestCaseConfigure(method, allocateMode, fileSize,  count, forceWrite, 1024*1024, threadCount))
    }
}