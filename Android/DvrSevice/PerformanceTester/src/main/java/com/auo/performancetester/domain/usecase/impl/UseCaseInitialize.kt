package com.auo.performancetester.domain.usecase.impl

import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.usecase.IUseCaseInitialize

class UseCaseInitialize(private val dataSource: IDataSource): IUseCaseInitialize {
    override fun invoke() =
        dataSource.initialize()
}