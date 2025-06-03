package com.auo.dvr_ui.presentation.contents.list

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.Presenter
import com.auo.dvr_ui.presentation.contents.list.component.ControlComponent
import com.auo.dvr_ui.presentation.contents.list.component.ListComponent
import com.auo.dvr_ui.presentation.contents.list.component.TabComponent
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale

internal class View(
    state: StateFlow<UiState>,
    effect: StateFlow<Effect?>,
    intentHandler: (UserIntent) -> Unit
) : Presenter.IView<UiState, UserIntent, Effect>(state, effect, intentHandler) {

    companion object {
        private val CONTROL_LAYER_HEIGHT = 96.dp
        private const val GRID_COLUMN_COUNT = 4
    }

    private val dateFormat: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    @Composable
    override fun Draw(modifier: Modifier) {
        val mState by state.collectAsState()
        val mEffect by effect.collectAsState()

        val context: Context = LocalContext.current

        val highlightColor = remember {
            Color(context.getColor(R.color.color_highlight))
        }

        val scrollState = rememberLazyGridState()

        LaunchedEffect(LocalContext.current) {
            intentHandler(UserIntent.Init)
        }

        Column(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            ) {
                TabComponent.TabLayer(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(Alignment.Center),
                    indicatorColor = highlightColor,
                    mState.displayType,
                    onDisplayTypeChanged = { intentHandler(UserIntent.DisplayTypeChanged(it)) }
                )

                if(mState.selectMode){
                    ControlComponent.SelectAllButtonLayer(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterEnd),
                        boardColor = highlightColor,
                        onClick = { intentHandler(UserIntent.SelectAll) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.weight(8f)) {
                    ListComponent.ListLayer(
                        modifier = Modifier.weight(1f),
                        scrollState = scrollState,
                        style = ListComponent.Style(
                            columnCount = GRID_COLUMN_COUNT,
                            itemStyle = ListComponent.ItemStyle(
                                highlightColor = highlightColor,
                                dateFormat = dateFormat
                            )
                        ),
                        selectMode = mState.selectMode,
                        list = mState.groupList,
                        selectedList = mState.selectedGroups,
                        onItemClicked = { intentHandler(UserIntent.ItemClicked(it)) }
                    )

                    ControlComponent.ControlLayer(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .align(alignment = Alignment.CenterHorizontally)
                            .height(CONTROL_LAYER_HEIGHT),
                        highlightColor = highlightColor,
                        selectedCount = mState.selectedGroups.size,
                        selectMode = mState.selectMode,
                        onSelectModeChanged = { intentHandler(UserIntent.SelectModeChanged(it)) },
                        onDeleteRequest = { intentHandler(UserIntent.Delete) },
                        onLockRequest = { intentHandler(UserIntent.Lock) },
                        onUnlockRequest = { intentHandler(UserIntent.Unlock) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}