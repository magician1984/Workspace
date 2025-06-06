package com.auo.dvr_ui.presentation.contents.list.component

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.R
import java.text.DateFormat
import java.util.Date

data object ListComponent {
    data class ItemStyle(val highlightColor: Color, val dateFormat: DateFormat)
    data class Style(val columnCount: Int, val itemStyle: ItemStyle)

    private val ITEM_CORNER_RADIUS = 20.dp
    private val ITEM_PADDING =
        PaddingValues(start = 26.dp, end = 26.dp, top = 15.dp, bottom = 15.dp)
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
            state = scrollState
        ) {
            items(list.size) { index ->
                val recordGroup = list[index]
                val selected = if (mSelectMode) selectedList.contains(recordGroup) else false

                Item(
                    modifier = Modifier.padding(ITEM_PADDING),
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
        val mShowCheckBox by remember(showSelectionLayer) {
            mutableStateOf(showSelectionLayer)
        }

        val mContext: Context = LocalContext.current

        val model = ImageRequest.Builder(mContext)
            .data(uri)
            .build()

        val mBorderColor by remember(selected) {
            mutableStateOf(
                if (selected) Color(mContext.getColor(R.color.color_primary_1)) else Color.Transparent
            )
        }

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(ITEM_CORNER_RADIUS))
                .border(ITEM_BORDER_WIDTH, mBorderColor, RoundedCornerShape(ITEM_CORNER_RADIUS))
        ) {
            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
            )

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

    private fun RecordGroup.getThumbnail(): Uri {
        return files[0].thumbnail
    }
}