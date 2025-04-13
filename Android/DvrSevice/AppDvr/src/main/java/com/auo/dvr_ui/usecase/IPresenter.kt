package com.auo.dvr_ui.usecase

import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseGetConfigure
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterDvrStateListener
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseSetConfigure
import com.auo.dvr_ui.entity.IUseCaseUnlockFile
import com.auo.dvr_ui.entity.IUseCaseUnmountStorage

interface IPresenter {

    fun summit(
        useCaseGetListFiles: IUseCaseGetListFiles,
        useCaseRegisterListener: IUseCaseRegisterListener,
        useCaseLockFile: IUseCaseLockFile,
        useCaseUnlockFile: IUseCaseUnlockFile,
        useCaseDeleteFile: IUseCaseDeleteFile,
        useCaseGetCacheFile: IUseCaseGetCacheFile,
        useCaseGetDvrState: IUseCaseGetDvrState,
        useCaseRegisterDvrStateListener: IUseCaseRegisterDvrStateListener,
        useCaseGetConfigure: IUseCaseGetConfigure,
        useCaseSetConfigure: IUseCaseSetConfigure,
        useCaseUnmountStorage: IUseCaseUnmountStorage
    )

    fun onReady()
    fun onLoading()
}