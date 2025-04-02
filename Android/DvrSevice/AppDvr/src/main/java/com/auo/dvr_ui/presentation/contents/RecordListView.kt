package com.auo.dvr_ui.presentation.contents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.R
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.presentation.IUserIntents
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class RecordListView(override val onIntent: (IUserIntents) -> Unit) : Presenter.IView {
    companion object{
        private const val NAME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }

    private val dateFormat : SimpleDateFormat = SimpleDateFormat(NAME_FORMAT, Locale.getDefault())

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Draw(
        modifier: Modifier,
        state: Presenter.State
    ) {
        val pageState = rememberPagerState(pageCount = { CamLocation.entries.size })

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = state.camLocation) {
            coroutineScope.launch {
                pageState.scrollToPage(CamLocation.entries.indexOf(state.camLocation))
            }
        }

        HorizontalPager(state = pageState, modifier = modifier) { page ->
            val subList =
                state.fileList.filter { it.location == CamLocation.entries[page] && if (state.isProtected) it.type == RecordType.Protected else it.type != RecordType.Protected }

            RecordList(
                modifier = Modifier.fillMaxSize(),
                recordList = subList,
                selectedFile = state.selectedFile,
                onIntent = onIntent
            )
        }
    }

    @Composable
    private fun RecordList(
        modifier: Modifier,
        recordList: List<RecordFileData>,
        selectedFile: RecordFileData?,
        onIntent: (IUserIntents) -> Unit
    ) {
        val itemHeight = (LocalConfiguration.current.screenHeightDp / 10).dp

        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            items(recordList) { record ->
                RecordItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    recordFileData = record,
                    isSelected = record.id == selectedFile?.id,
                    onIntent = onIntent
                )
            }
        }
    }

    @Composable
    private fun RecordItem(
        modifier: Modifier,
        recordFileData: RecordFileData,
        isSelected: Boolean = false,
        onIntent: (IUserIntents) -> Unit,
    ) {
        val lockerPainterRes = remember(recordFileData.type) {
            if (recordFileData.type == RecordType.Locked)
                R.drawable.baseline_lock_outline_24
            else
                R.drawable.baseline_lock_open_24
        }

        val deletePainterRes = remember {
            R.drawable.baseline_delete_outline_24
        }

        val scale by animateFloatAsState(if (isSelected) 1.01f else 1f, label = "item selected")

        Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale),
            onClick = {
                onIntent(
                    if (isSelected) IUserIntents.UnselectFile else IUserIntents.SelectFile(
                        recordFileData
                    )
                )
            },
            border = BorderStroke(2.dp, if (isSelected) Color.Red else Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(recordFileData.type == RecordType.Protected){
                    // No show icon
                    Spacer(modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(4.dp))
                }else{
                    Icon(painter = painterResource(lockerPainterRes),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clickable {
                                if (recordFileData.type == RecordType.Locked)
                                    onIntent(IUserIntents.UnlockFile(recordFileData))
                                else
                                    onIntent(IUserIntents.LockFile(recordFileData))
                            })
                }

                Text(text = dateFormat.format(Date(recordFileData.createTime)), modifier = Modifier.weight(1f), fontSize = 48.sp, textAlign = TextAlign.Center)
                Icon(painter = painterResource(deletePainterRes),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clickable { onIntent(IUserIntents.DeleteFile(recordFileData)) })
            }
        }
    }
}