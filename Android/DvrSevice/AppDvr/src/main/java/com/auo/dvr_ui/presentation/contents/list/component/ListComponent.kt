package com.auo.dvr_ui.presentation.contents.list.component

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auo.dvr_core.RecordGroup
import java.text.DateFormat
import java.util.Date

data object ListComponent {
    data class ItemStyle(val highlightColor: Color, val dateFormat: DateFormat)
    data class Style(val columnCount: Int, val itemStyle: ItemStyle)

    private val ITEM_CORNER_RADIUS = 16.dp
    private val ITEM_PADDING = 8.dp
    private const val ITEM_ASPECT_RATIO = 4f / 3f

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

        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(style.columnCount),
            state = scrollState
        ) {
            items(list.size) { index ->
                val recordGroup = list[index]

                if(selectMode){
                    ItemInSelectMode(
                        modifier = Modifier,
                        item = recordGroup,
                        selected = selectedList.contains(recordGroup),
                        style = mStyle.itemStyle,
                        onItemClicked = onItemClicked
                    )
                }else{
                    Item(
                        modifier = Modifier,
                        style = mStyle.itemStyle,
                        onItemClicked = onItemClicked,
                        item = recordGroup
                    )
                }
            }
        }
    }

    @Composable
    private fun Item(
        modifier: Modifier,
        item: RecordGroup,
        style: ItemStyle,
        onItemClicked: (RecordGroup) -> Unit
    ) {
        val mStyle by remember(style) {
            mutableStateOf(style)
        }

        val model = ImageRequest.Builder(LocalContext.current)
            .data(item.getThumbnail())
            .build()

        Column(modifier = modifier
            .padding(ITEM_PADDING)
            .clickable { onItemClicked(item) }) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(ITEM_ASPECT_RATIO)
                    .clip(RoundedCornerShape(ITEM_CORNER_RADIUS))
            )
            Text(text = mStyle.dateFormat.format(Date(item.timestamp)))
        }
    }

    @Composable
    private fun ItemInSelectMode(
        modifier: Modifier,
        item: RecordGroup,
        selected: Boolean,
        style: ItemStyle,
        onItemClicked: (RecordGroup) -> Unit
    ){
        val mStyle by remember(style) {
            mutableStateOf(style)
        }

        val mBoardColor by remember(selected) {
            mutableStateOf(if (selected) mStyle.highlightColor else Color.Transparent)
        }

        val model = ImageRequest.Builder(LocalContext.current)
            .data(item.getThumbnail())
            .build()

        Column(modifier = modifier
            .padding(ITEM_PADDING)
            .clickable { onItemClicked(item) }) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(ITEM_ASPECT_RATIO)
                    .clip(RoundedCornerShape(ITEM_CORNER_RADIUS))
                    .border(4.dp, mBoardColor, RoundedCornerShape(ITEM_CORNER_RADIUS))
            )
            Text(text = mStyle.dateFormat.format(Date(item.timestamp)))
        }
    }

    private fun RecordGroup.getThumbnail() : Uri{
        return files[0].thumbnail
    }
}