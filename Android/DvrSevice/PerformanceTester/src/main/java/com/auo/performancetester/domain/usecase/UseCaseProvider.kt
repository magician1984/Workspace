package com.auo.performancetester.domain.usecase

import android.app.Activity
import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.dvr_core.DvrException
import com.auo.performancetester.domain.usecase.impl.UseCaseExit
import com.auo.performancetester.domain.usecase.impl.UseCaseInitialize
import com.auo.performancetester.domain.usecase.impl.UseCaseListenEvents
import com.auo.performancetester.domain.usecase.impl.UseCaseStartTest
import kotlin.reflect.KClass

class UseCaseProvider(private val activity: Activity, private val dataSource:IDataSource) {
    fun <T : IUseCase> get(clazz: KClass<T>): T {
        return when (clazz) {
            IUseCaseExit::class -> UseCaseExit(activity) as T
            IUseCaseListenEvents::class -> UseCaseListenEvents(dataSource) as T
            IUseCaseStartTest::class -> UseCaseStartTest(dataSource) as T
            IUseCaseInitialize::class -> UseCaseInitialize(dataSource) as T
            else -> throw com.auo.dvr_core.DvrException("UseCaseProvider", "Unsupported")
        }
    }
}