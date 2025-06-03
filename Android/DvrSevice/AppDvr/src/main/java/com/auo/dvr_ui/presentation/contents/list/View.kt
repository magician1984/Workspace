package com.auo.dvr_ui.presentation.contents.list

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class View(
    state: StateFlow<UiState>,
    effect: StateFlow<Effect?>,
    intentHandler: (UserIntent) -> Unit
) : Presenter.IView<UiState, UserIntent, Effect>(state, effect, intentHandler) {

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
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()){
                TabLayer(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(Alignment.Center),
                    indicatorColor = highlightColor,
                    mState.displayType
                ) {
                    intentHandler(UserIntent.DisplayTypeChanged(it))
                }

                SelectAllButtonLayer(modifier = Modifier.wrapContentWidth().align(Alignment.CenterEnd), mState.selectMode) {
                    intentHandler(UserIntent.SelectAll)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier.weight(8f)) {
                    ListLayer(
                        modifier = Modifier
                            .weight(1f),
                        scrollState = scrollState,
                        highlightColor = highlightColor,
                        selectMode = mState.selectMode,
                        list = mState.groupList,
                        selectedList = mState.selectedGroups
                    ) {

                    }
                    ControlLayer(
                        modifier = Modifier.fillMaxWidth(),
                        mState.selectMode
                    ) {

                    }
                }
                ScrollBarLayer(modifier = Modifier.weight(1f), scrollState = scrollState, trackColor = highlightColor, backgroundColor = Color.DarkGray)
            }
        }
    }

    @Composable
    private fun TabLayer(
        modifier: Modifier,
        indicatorColor: Color,
        displayType: UiState.DisplayType,
        onDisplayTypeChanged: (UiState.DisplayType) -> Unit
    ) {

        val tabs = remember {
            buildList { UiState.DisplayType.entries.forEach { type -> add(type.name) } }
        }

        val selectedIndex by remember(displayType) {
            mutableIntStateOf(displayType.code)
        }

        TabRow(modifier = modifier, selectedTabIndex = selectedIndex) {
            tabs.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                Tab(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected)
                            onDisplayTypeChanged(UiState.DisplayType.entries[index])
                    },
                    text = { Text(text = label) },
                    selectedContentColor = indicatorColor,
                    unselectedContentColor = Color.White
                )
            }
        }
    }

    @Composable
    private fun ListLayer(
        modifier: Modifier,
        scrollState: LazyGridState,
        highlightColor: Color,
        selectMode: Boolean,
        list: List<RecordGroup>,
        selectedList: List<RecordGroup>,
        onItemClicked: (RecordGroup) -> Unit
    ) {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(4),
            state = scrollState
        ) {
            items(list.size) { index ->
                Item(
                    modifier = Modifier,
                    highlightColor = highlightColor,
                    selectMode = selectMode,
                    item = list[index]
                )
            }
        }
    }

    @Composable
    private fun ControlLayer(
        modifier: Modifier,
        selectMode: Boolean,
        onSelectModeChanged: (Boolean) -> Unit
    ) {
        Box(modifier = modifier.padding(16.dp)) {
            if (selectMode) {

            } else {
                OutlinedButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { onSelectModeChanged(true) },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Select Recording", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    @Composable
    private fun ScrollBarLayer(modifier: Modifier, scrollState: LazyGridState, trackColor : Color, backgroundColor : Color){
        val density = LocalDensity.current
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp

        Box(
            modifier = modifier
                .drawWithContent {
                    drawContent()
                    val layoutInfo = scrollState.layoutInfo
                    val totalItems = layoutInfo.totalItemsCount
                    val visibleItems = layoutInfo.visibleItemsInfo.size


                    if (totalItems == 0 || visibleItems == 0 || totalItems <= visibleItems) return@drawWithContent

                    // Compute scroll progress (0f to 1f)
                    val scrollProgress = scrollState.firstVisibleItemIndex / (totalItems - visibleItems).toFloat()

                    // Compute thumb height and position
                    val trackHeight = size.height
                    val thumbHeight = (visibleItems / totalItems.toFloat()) * trackHeight
                    val thumbOffset = scrollProgress * (trackHeight - thumbHeight)

                    val thumbWidth = 6.dp.toPx()
                    val trackWidth = 2.dp.toPx()
                    val endPadding = 8.dp.toPx()
                    val cornerRadius = CornerRadius(4.dp.toPx())

                    // Draw track (thin background bar)
                    drawRoundRect(
                        color = backgroundColor,
                        topLeft = Offset(size.width - endPadding, 0f),
                        size = Size(trackWidth, trackHeight),
                        cornerRadius = cornerRadius
                    )

                    // Draw thumb
                    drawRoundRect(
                        color = trackColor,
                        topLeft = Offset(size.width - endPadding, thumbOffset),
                        size = Size(thumbWidth, thumbHeight),
                        cornerRadius = cornerRadius
                    )
                }
        )
    }

    @Composable
    private fun SelectAllButtonLayer(modifier: Modifier, selectMode: Boolean, onClick : () -> Unit){
        Box(modifier = modifier){
            if(selectMode){
                OutlinedButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = onClick,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "SelectAll", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    @Composable
    private fun Item(
        modifier: Modifier,
        highlightColor: Color,
        selectMode: Boolean,
        item: RecordGroup
    ) {
        val model = ImageRequest.Builder(LocalContext.current)
            .data(item.files[0].thumbnail)
            .build()

        Column(modifier = modifier.padding(8.dp)) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(4.dp, highlightColor, RoundedCornerShape(16.dp))
            )
            Text(text = dateFormat.format(Date(item.timestamp)))
        }
    }

}