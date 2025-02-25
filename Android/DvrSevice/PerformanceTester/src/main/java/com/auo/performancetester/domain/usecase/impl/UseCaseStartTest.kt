package com.auo.performancetester.domain.usecase.impl

import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.usecase.IUseCaseStartTest

class UseCaseStartTest(private val testSource: IDataSource) : IUseCaseStartTest {
    override fun invoke(method: CloneMethod, size: Long, count: Int) {
        testSource.startTest(method, size, count)
    }
}