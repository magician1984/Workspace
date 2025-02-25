package com.auo.performancetester.domain.usecase.impl

import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.IData
import com.auo.performancetester.domain.usecase.IUseCaseListenEvents

class UseCaseListenEvents(private val dataSource: IDataSource) : IUseCaseListenEvents {
    override fun invoke(callback: (IData) -> Unit) {
        dataSource.eventListener = IDataSource.EventListener { data -> callback(data) }
    }
}