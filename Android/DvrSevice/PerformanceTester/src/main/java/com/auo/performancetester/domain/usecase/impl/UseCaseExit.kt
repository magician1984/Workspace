package com.auo.performancetester.domain.usecase.impl

import android.app.Activity
import com.auo.performancetester.domain.usecase.IUseCaseExit

class UseCaseExit(private val context: Activity) : IUseCaseExit {
    override fun invoke() {
        context.finish()
    }
}