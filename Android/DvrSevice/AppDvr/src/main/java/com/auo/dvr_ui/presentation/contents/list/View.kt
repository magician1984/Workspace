package com.auo.dvr_ui.presentation.contents.list

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.LazyGridLayoutInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.Presenter
import com.auo.dvr_ui.presentation.contents.list.component.ControlComponent
import com.auo.dvr_ui.presentation.contents.list.component.EffectComponent
import com.auo.dvr_ui.presentation.contents.list.component.ListComponent
import com.auo.dvr_ui.presentation.contents.list.component.TabComponent
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

internal class View(
    state: StateFlow<UiState>,
    effect: StateFlow<Effect?>,
    intentHandler: (UserIntent) -> Unit
) : Presenter.IView<UiState, UserIntent, Effect>(state, effect, intentHandler) {

    companion object {
        private val CONTROL_CONTAINER_HEIGHT = 166.dp
        private val CONTROL_LAYER_HEIGHT = 100.dp
        private val CONTROL_LAYER_WIDTH = 1165.dp
        private val CONTROL_LAYER_PADDING =
            PaddingValues(start = 44.dp, top = 13.dp, bottom = 13.dp, end = 45.dp)
        private val CONTROL_LAYER_BTN_SPACE = 108.dp
        private val CONTROL_LAYER_TEXT_SIZE = 32.sp
        private val TOGGLE_WIDTH = 428.dp
        private val TOGGLE_HEIGHT = 80.dp
        private val TOGGLE_TEXT_SIZE = 35.sp
        private val TOGGLE_TEXT_COLOR = Color.White
        private val TOGGLE_BORDER_WIDTH = 4.dp
        private val LIST_PADDING = PaddingValues(start = 149.dp, end = 149.dp)
        private val TAB_LABEL_SIZE = 42.sp
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
            Color(context.getColor(R.color.color_primary_1))
        }

        val scrollState = rememberLazyGridState()

        Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LIST_PADDING)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 26.dp, end = 26.dp, top = 42.dp)
                ) {
                    TabComponent.TabLayer(
                        modifier = Modifier
                            .width(864.dp)
                            .align(Alignment.Center),
                        indicatorColor = highlightColor,
                        labelSize = TAB_LABEL_SIZE,
                        displayType = mState.displayType,
                        onDisplayTypeChanged = { intentHandler(UserIntent.DisplayTypeChanged(it)) }
                    )

                    ControlComponent.SelectAllButtonLayer(
                        modifier = Modifier
                            .size(266.dp, 81.dp)
                            .align(Alignment.CenterEnd),
                        boardColor = highlightColor,
                        enable = mState.selectMode,
                        onClick = { intentHandler(UserIntent.SelectAll) }
                    )
                }

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
                        .fillMaxWidth()
                        .padding(end = 100.dp)
                        .height(CONTROL_CONTAINER_HEIGHT)
                        .align(alignment = Alignment.CenterHorizontally),
                    selectedCount = mState.selectedGroups.size,
                    selectMode = mState.selectMode,
                    config = ControlComponent.ControlLayerConfig(
                        controlLayerWidth = CONTROL_LAYER_WIDTH,
                        controlLayerHeight = CONTROL_LAYER_HEIGHT,
                        innerPadding = CONTROL_LAYER_PADDING,
                        btnSpace = CONTROL_LAYER_BTN_SPACE,
                        textSize = CONTROL_LAYER_TEXT_SIZE,
                        selectModeToggleConfig = ControlComponent.SelectModeToggleConfig(
                            width = TOGGLE_WIDTH,
                            height = TOGGLE_HEIGHT,
                            textSize = TOGGLE_TEXT_SIZE,
                            textColor = TOGGLE_TEXT_COLOR,
                            backgroundColor = Color.Transparent,
                            borderColor = highlightColor,
                            borderWidth = TOGGLE_BORDER_WIDTH
                        )
                    ),
                    onSelectModeChanged = { intentHandler(UserIntent.SelectModeChanged(it)) },
                    onDeleteRequest = { intentHandler(UserIntent.DeleteRequest) },
                    onLockRequest = { intentHandler(UserIntent.Lock) },
                    onUnlockRequest = { intentHandler(UserIntent.Unlock) }
                )
            }

            Scrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(8.dp, 800.dp)
                    .offset(x = (-59).dp),
                scrollState = scrollState,
                backgroundColor = Color.DarkGray,
                trackColor = highlightColor,
                trackHeight = 80.dp
            )
        }

        EffectHandler(modifier = Modifier.fillMaxSize(), effect = mEffect)
    }

    @Composable
    private fun EffectHandler(modifier: Modifier, effect: Effect?) {
        when (effect) {
            is Effect.ConfirmDelete -> EffectComponent.DeleteConfirm(
                onConfirmed = {
                    intentHandler(UserIntent.Delete)
                },
                onDismiss = {
                    intentHandler(UserIntent.CancelDelete)
                },
            )

            null -> {}
        }
    }

    @Composable
    fun Scrollbar(
        modifier: Modifier = Modifier,
        scrollState: LazyGridState,
        backgroundColor: Color,
        trackColor: Color,
        trackHeight: Dp
    ) {
        printLazyGridState(scrollState)
        val layoutInfo = scrollState.layoutInfo
        val density = LocalDensity.current
        val viewportHeightPx = layoutInfo.viewportSize.height.toFloat()

        // 計算 scrollFraction — 使用真實 item offset 與 viewport range
        val scrollFraction by remember {
            derivedStateOf {
                val items = layoutInfo.visibleItemsInfo
                if (items.isEmpty()) return@derivedStateOf 0f

                val firstItemOffset = items.first().offset
                val scrollOffset = layoutInfo.viewportStartOffset - firstItemOffset.y

                val lastItem = items.last()
                val totalContentHeight = lastItem.offset.y + lastItem.size.height
                val scrollableHeight = (totalContentHeight - viewportHeightPx).coerceAtLeast(1f)

                (scrollOffset / scrollableHeight).coerceIn(0f, 1f)
            }
        }

        // 滑塊 offset 動畫
        val animatedOffsetY = remember { Animatable(0f) }

        LaunchedEffect(scrollFraction, viewportHeightPx, trackHeight) {
            val trackHeightPx = with(density) { trackHeight.toPx() }
            val targetOffset = (viewportHeightPx - trackHeightPx) * scrollFraction
            animatedOffsetY.animateTo(
                targetValue = targetOffset,
                animationSpec = tween(durationMillis = 80, easing = LinearEasing)
            )
        }

        // UI 畫出 scrollbar
        Box(
            modifier = modifier
                .width(8.dp)
                .fillMaxHeight()
                .background(backgroundColor, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .offset { IntOffset(0, animatedOffsetY.value.roundToInt()) }
                    .background(trackColor, RoundedCornerShape(50))
            )
        }
    }

    fun printLazyGridState(state: LazyGridState) {
        val tag = "Scroll"
        Log.d(tag, "===== LazyGridState =====")
        Log.d(tag, "firstVisibleItemIndex: ${state.firstVisibleItemIndex}")
        Log.d(tag, "firstVisibleItemScrollOffset: ${state.firstVisibleItemScrollOffset}")
        Log.d(tag, "isScrollInProgress: ${state.isScrollInProgress}")
        Log.d(tag, "canScrollForward: ${state.canScrollForward}")
        Log.d(tag, "canScrollBackward: ${state.canScrollBackward}")
        Log.d(tag, "lastScrolledForward: ${state.lastScrolledForward}")
        Log.d(tag, "lastScrolledBackward: ${state.lastScrolledBackward}")

        val layoutInfo = state.layoutInfo
        Log.d(tag, "--- LayoutInfo summary ---")
        Log.d(tag, "mainAxisItemSpacing: ${layoutInfo.mainAxisItemSpacing}")
        Log.d(tag, "totalItemsCount: ${layoutInfo.totalItemsCount}")
        Log.d(tag, "viewportSize: ${layoutInfo.viewportSize.width} x ${layoutInfo.viewportSize.height}")
        Log.d(tag, "visibleItems count: ${layoutInfo.visibleItemsInfo.size}")
        Log.d(tag, "viewportStartOffset: ${layoutInfo.viewportStartOffset}")
        Log.d(tag, "viewportEndOffset: ${layoutInfo.viewportEndOffset}")
        Log.d(tag, "reverseLayout: ${layoutInfo.reverseLayout}")
        Log.d(tag, "orientation: ${layoutInfo.orientation}")
        Log.d(tag, "maxSpan: ${layoutInfo.maxSpan}")
        if(layoutInfo.visibleItemsInfo.isNotEmpty()){
            Log.d(tag, "item height: ${layoutInfo.visibleItemsInfo[0].size.height}")
        }
        Log.d(tag, "===========================")
    }
}