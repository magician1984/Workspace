package com.auo.dvr_ui.presentation.contents

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.auo.dvr_ui.presentation.IUserIntents
import com.auo.dvr_ui.presentation.Presenter

internal class SettingsView(override val onIntent: (IUserIntents) -> Unit) : Presenter.IView {
    @Composable
    override fun Draw(modifier: Modifier, state: Presenter.State) {
        TODO("Not yet implemented")
    }
}