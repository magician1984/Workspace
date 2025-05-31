package com.auo.dvr_ui.presentation.contents

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.TintableBackgroundView
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.presentation.IUserIntents
import com.auo.dvr_ui.presentation.Presenter
import java.text.SimpleDateFormat
import java.util.Locale

internal class RecordListView(override val onIntent: (IUserIntents) -> Unit) : Presenter.IView {
    companion object {
        private const val NAME_FORMAT = "yyyy-MM-dd HH:mm:ss"
        private const val ITEM_COLUMN_COUNT = 6
    }

    private val dateFormat: SimpleDateFormat = SimpleDateFormat(NAME_FORMAT, Locale.getDefault())

    @Composable
    override fun Draw(
        modifier: Modifier,
        state: Presenter.State
    ) {

    }

    @Composable
    private fun RecordItem(modifier: Modifier, group : RecordGroup) {

    }
}