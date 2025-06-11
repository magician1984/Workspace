package com.auo.dvr_ui.presentation.contents.list

import com.auo.dvr_ui.presentation.Presenter

sealed class Effect : Presenter.IEffect{
    data object ConfirmDelete : Effect()
}