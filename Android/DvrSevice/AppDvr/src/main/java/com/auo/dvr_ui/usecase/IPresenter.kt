package com.auo.dvr_ui.usecase

import com.auo.dvr_ui.entity.IUseCase

interface IPresenter {

    fun summitUseCases(vararg useCases: IUseCase)

    fun onReady()
    fun onLoading()
}