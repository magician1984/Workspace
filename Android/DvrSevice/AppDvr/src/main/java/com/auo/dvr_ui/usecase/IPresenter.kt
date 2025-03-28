package com.auo.dvr_ui.usecase

import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterListener

interface IPresenter {
    val useCaseRegisterListener : IUseCaseRegisterListener
    val useCaseLockFile : IUseCaseLockFile
    val useCaseUnlockFile : IUseCaseLockFile
    val useCaseDeleteFile : IUseCaseDeleteFile
    val useCaseGetCacheFile : IUseCaseGetCacheFile
}