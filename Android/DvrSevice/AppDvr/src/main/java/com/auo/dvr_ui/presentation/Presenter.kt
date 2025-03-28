package com.auo.dvr_ui.presentation

import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.usecase.IPresenter

class Presenter(
    override val useCaseRegisterListener: IUseCaseRegisterListener,
    override val useCaseLockFile: IUseCaseLockFile,
    override val useCaseUnlockFile: IUseCaseLockFile,
    override val useCaseDeleteFile: IUseCaseDeleteFile,
    override val useCaseGetCacheFile: IUseCaseGetCacheFile
) : IPresenter {
}