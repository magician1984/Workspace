package com.auo.dvr_ui.presentation.contents.list.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

data object ListComponent {
    data class ItemStyle(val highlightColor: Color, val dateFormat: DateFormat)
    data class Style(val columnCount: Int, val itemStyle: ItemStyle)

    private val ITEM_CORNER_RADIUS = 20.dp
    private val ITEM_VERTICAL_PADDING = 15.dp
    private val ITEM_HORIZONTAL_PADDING = 26.dp
    private const val ITEM_ASPECT_RATIO = 4f / 3f
    private val ITEM_BORDER_WIDTH = 7.dp
    private val ITEM_TEXT_SIZE = 24.sp

    @Composable
    fun ListLayer(
        modifier: Modifier,
        scrollState: LazyGridState,
        style: Style,
        selectMode: Boolean,
        list: List<RecordGroup>,
        selectedList: List<RecordGroup>,
        onItemClicked: (RecordGroup) -> Unit
    ) {
        val mStyle by remember(style) {
            mutableStateOf(style)
        }

        val mSelectMode by remember(selectMode) {
            mutableStateOf(selectMode)
        }

        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(style.columnCount),
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(ITEM_VERTICAL_PADDING),
            horizontalArrangement = Arrangement.spacedBy(ITEM_HORIZONTAL_PADDING)
        ) {
            items(list.size) { index ->
                val recordGroup = list[index]
                val selected = if (mSelectMode) selectedList.contains(recordGroup) else false

                Item(
                    modifier = Modifier,
                    item = recordGroup,
                    selectMode = mSelectMode,
                    style = mStyle.itemStyle,
                    selected = selected,
                    onItemClicked = onItemClicked
                )
            }
        }
    }

    @Composable
    private fun Item(
        modifier: Modifier,
        item: RecordGroup,
        selectMode: Boolean = false,
        selected: Boolean = false,
        style: ItemStyle,
        onItemClicked: (RecordGroup) -> Unit
    ) {
        val mStyle by remember(style) {
            mutableStateOf(style)
        }

        Column(
            modifier = modifier
                .clickable { onItemClicked(item) }) {
            ItemImage(
                modifier = Modifier.aspectRatio(ITEM_ASPECT_RATIO),
                uri = item.getThumbnail(),
                showSelectionLayer = selectMode,
                selected = selected
            )
            ItemLabel(
                modifier = Modifier,
                text = mStyle.dateFormat.format(Date(item.timestamp)),
                locked = item.type == RecordType.Locked
            )
        }
    }

    @Composable
    private fun ItemImage(
        modifier: Modifier,
        uri: Uri,
        showSelectionLayer: Boolean = false,
        selected: Boolean = false
    ) {
        val context = LocalContext.current

        // ✅ 記住快取用的 bitmap
        var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }

        // ✅ 背景執行解碼並快取
        LaunchedEffect(uri) {
            withContext(Dispatchers.IO) {
                val bmp = uri.toBitmap(context, width = 1920, height = 1280) // << 替換成你的解碼邏輯
                bitmap = bmp
            }
        }

        // ✅ 判斷選取框色彩
        val mBorderColor by remember(selected) {
            mutableStateOf(
                if (selected) Color(context.getColor(R.color.color_primary_1)) else Color.Transparent
            )
        }

        val mShowCheckBox by remember(showSelectionLayer) {
            mutableStateOf(showSelectionLayer)
        }

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(ITEM_CORNER_RADIUS))
                .border(ITEM_BORDER_WIDTH, mBorderColor, RoundedCornerShape(ITEM_CORNER_RADIUS))
        ) {
            // ✅ 顯示 Bitmap（已解碼 or 快取）
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 疊圖
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_stack),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset((-20).dp, 17.dp),
                contentDescription = null
            )

            if (mShowCheckBox) {
                val mBoxRes by remember(selected) {
                    mutableIntStateOf(if (selected) R.drawable.selectbox_select else R.drawable.selectbox)
                }

                Image(
                    imageVector = ImageVector.vectorResource(mBoxRes),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(20.dp, 17.dp),
                    contentDescription = null
                )
            }
        }
    }

    @Composable
    private fun ItemLabel(modifier: Modifier, text: String, locked: Boolean) {
        val mLocked by remember(locked) {
            mutableStateOf(locked)
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text(
                text = text,
                fontSize = ITEM_TEXT_SIZE
            )

            Image(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .then(if (mLocked) Modifier else Modifier.alpha(0f)),
                imageVector = ImageVector.vectorResource(R.drawable.ic_dvr_lock),
                contentDescription = null
            )
        }
    }

    @Composable
    fun Scrollbar(
        modifier: Modifier = Modifier,
        scrollState: LazyGridState,
        backgroundColor: Color,
        trackColor: Color,
        thumbHeight: Dp = 48.dp // 固定滑塊高度
    ) {
        if(scrollState.layoutInfo.totalItemsCount == 0) return

        val layoutInfo = scrollState.layoutInfo
        val itemCount = layoutInfo.totalItemsCount
        val spanCount = layoutInfo.maxSpan.coerceAtLeast(1)

        val totalRowCount = (itemCount / spanCount) + 0
        val averageItemHeight = layoutInfo.visibleItemsInfo
            .map { it.size.height }
            .takeIf { it.isNotEmpty() }
            ?.average()?.toFloat() ?: 1f

        val itemSpacing = -ITEM_VERTICAL_PADDING.value
//        val itemSpacing = layoutInfo.mainAxisItemSpacing
        val totalContentHeight = totalRowCount * averageItemHeight + (totalRowCount - 1) * itemSpacing

        val viewportHeightPx = layoutInfo.viewportSize.height.toFloat()
        val scrollableRange = (totalContentHeight - viewportHeightPx).coerceAtLeast(1f)

        val thumbHeightPx = with(LocalDensity.current) { thumbHeight.toPx() }
        val movableRange = (viewportHeightPx - thumbHeightPx).coerceAtLeast(0f)

        val scrollOffset = (scrollState.firstVisibleItemIndex / layoutInfo.maxSpan) * averageItemHeight + scrollState.firstVisibleItemScrollOffset
        val scrollFraction = (movableRange / scrollableRange).coerceIn(0f, 1f)
        val scrollbarOffsetY = scrollFraction * scrollOffset

        Box(
            modifier = modifier
                .width(8.dp)
                .fillMaxHeight()
                .background(backgroundColor, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(thumbHeight)
                    .offset { IntOffset(0, scrollbarOffsetY.roundToInt()) }
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

    fun Uri.toBitmap(context: Context, width: Int, height: Int): Bitmap? {
        // 產生快取檔名
        val cacheKey = "${this.hashCode()}_${width}x$height.jpg"
        val cacheFile = File(context.cacheDir, cacheKey)

        // 已存在快取就直接讀取
        if (cacheFile.exists()) {
            return BitmapFactory.decodeFile(cacheFile.absolutePath)
        }

        // 嘗試讀取原始 NV12 檔案
        val inputStream = context.contentResolver.openInputStream(this) ?: return null
        val nv12Bytes = inputStream.use { it.readBytes() }

        // 轉成 NV21（UV 交錯順序：NV12 = UVUV, NV21 = VUVU）
        val nv21Bytes = nv12ToNv21(nv12Bytes, width, height)

        // 建立 YuvImage 並壓成 JPEG
        val yuvImage = YuvImage(nv21Bytes, ImageFormat.NV21, width, height, null)
        val jpegOut = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 90, jpegOut)

        // 儲存至 cache folder
        val jpegBytes = jpegOut.toByteArray()
        FileOutputStream(cacheFile).use { it.write(jpegBytes) }

        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
    }

    private fun RecordGroup.getThumbnail(): Uri {
        return files[0].thumbnail
    }

    private fun nv12ToNv21(src: ByteArray, width: Int, height: Int): ByteArray {
        val frameSize = width * height
        val nv21 = ByteArray(frameSize * 3 / 2)

        // Copy Y plane
        System.arraycopy(src, 0, nv21, 0, frameSize)

        // Convert UV (NV12) → VU (NV21)
        var i = 0
        while (i < frameSize / 2) {
            val u = src[frameSize + i]
            val v = src[frameSize + i + 1]
            nv21[frameSize + i] = v
            nv21[frameSize + i + 1] = u
            i += 2 // 手動步進
        }

        return nv21
    }
}