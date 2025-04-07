package com.auo.dvr_ui.presentation.contents

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    companion object {
        private const val NAME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }

    private sealed class IFileControlIntent(val file: RecordFileData) {
        class Delete(file: RecordFileData) : IFileControlIntent(file)
        class LockSwitch(file: RecordFileData) : IFileControlIntent(file)
        class SelectSwitch(file: RecordFileData) : IFileControlIntent(file)
    }

    private val tabItems = listOf(
        Pair(CamLocation.Front, R.drawable.baseline_filter_1_24),
        Pair(CamLocation.Rear, R.drawable.baseline_filter_2_24),
        Pair(CamLocation.Left, R.drawable.baseline_filter_3_24),
        Pair(CamLocation.Right, R.drawable.baseline_filter_4_24),
    )

    private fun interface IFileControlHandler {
        fun onIntent(intent: IFileControlIntent)
    }

    private val dateFormat: SimpleDateFormat = SimpleDateFormat(NAME_FORMAT, Locale.getDefault())

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Draw(
        modifier: Modifier,
        state: Presenter.State
    ) {


        var selectedCamLocation by remember {
            mutableStateOf(state.camLocation)
        }

        var triggerFromTab by remember {
            mutableStateOf(false)
        }

        val pageState = rememberPagerState(pageCount = { CamLocation.entries.size })

        LaunchedEffect(selectedCamLocation) {
            onIntent(IUserIntents.ViewCameraLocation(selectedCamLocation))
        }

        Column(modifier = modifier) {
            TabLayout(
                modifier = Modifier.wrapContentHeight(),
                pagerState = pageState,
                selectedCamLocation = selectedCamLocation,
                onSelectedChanged = {
                    triggerFromTab = true
                    selectedCamLocation = it
                })

            ListLayout(
                modifier = Modifier.weight(1f),
                pagerState = pageState,
                files = state.fileList,
                selectedFile = state.selectedFile,
                selectedCamLocation = selectedCamLocation,
                onCamLocationChanged = {
                    Log.d(
                        "RecordListView",
                        "onCamLocationChanged: (changed : $it, triggerFromTab : $triggerFromTab, selectedCamLocation : $selectedCamLocation)"
                    )
                    if (!triggerFromTab)
                        selectedCamLocation = it
                    else if (it == selectedCamLocation)
                        triggerFromTab = false
                }
            ) {
                val file = it.file
                val userIntent = when (it) {
                    is IFileControlIntent.Delete -> {
                        if (file.type == RecordType.Protected)
                            IUserIntents.ConfirmDeleteFile(file)
                        else
                            IUserIntents.DeleteFile(file)
                    }

                    is IFileControlIntent.LockSwitch -> {
                        if (file.type == RecordType.Locked)
                            IUserIntents.UnlockFile(file)
                        else
                            IUserIntents.LockFile(file)
                    }

                    is IFileControlIntent.SelectSwitch -> {
                        if (state.selectedFile?.id == file.id)
                            IUserIntents.UnselectFile
                        else
                            IUserIntents.SelectFile(file)
                    }
                }
                onIntent(userIntent)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun TabLayout(
        modifier: Modifier,
        pagerState : PagerState,
        selectedCamLocation: CamLocation,
        onSelectedChanged: (CamLocation) -> Unit
    ) {
        val selectedTabIndex = tabItems.indexOfFirst { it.first == selectedCamLocation }

        TabRow(modifier = modifier, selectedTabIndex = selectedTabIndex, indicator = {tabPositions ->
            TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]))
        }) {
            tabItems.forEachIndexed { index, pair ->
                val isSelected = index == selectedTabIndex
                Tab(
                    modifier = Modifier.fillMaxHeight(),
                    selected = isSelected,
                    onClick = {
                        if (!isSelected)
                            onSelectedChanged(pair.first)
                    }) {
                    Icon(
                        painter = painterResource(id = pair.second),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 4.dp, top = 4.dp)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ListLayout(
        modifier: Modifier,
        pagerState: PagerState,
        files: List<RecordFileData>,
        selectedFile: RecordFileData?,
        selectedCamLocation: CamLocation,
        onCamLocationChanged: (CamLocation) -> Unit,
        handler: IFileControlHandler
    ) {
        val coroutineScope = rememberCoroutineScope()

        val selectedIndex = CamLocation.entries.indexOf(selectedCamLocation)

        LaunchedEffect(selectedIndex) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(selectedIndex)
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            coroutineScope.launch {
                onCamLocationChanged(CamLocation.entries[pagerState.currentPage])
            }
        }

        HorizontalPager(state = pagerState, modifier = modifier) { page ->
            val subList =
                files.filter { it.location == CamLocation.entries[page] }

            RecordList(
                modifier = Modifier.fillMaxSize(),
                recordList = subList,
                selectedFile = selectedFile,
                handler = handler
            )
        }
    }

    @Composable
    private fun RecordList(
        modifier: Modifier,
        recordList: List<RecordFileData>,
        selectedFile: RecordFileData?,
        handler: IFileControlHandler
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
                    handler = handler
                )
            }
        }
    }

    @Composable
    private fun RecordItem(
        modifier: Modifier,
        recordFileData: RecordFileData,
        isSelected: Boolean = false,
        handler: IFileControlHandler
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
                handler.onIntent(IFileControlIntent.SelectSwitch(recordFileData))
            },
            border = BorderStroke(2.dp, if (isSelected) Color.Red else Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (recordFileData.type == RecordType.Protected) {
                    // No show icon
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .padding(4.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(lockerPainterRes),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clickable {
                                handler.onIntent(IFileControlIntent.LockSwitch(recordFileData))
                            })
                }

                Text(
                    text = dateFormat.format(Date(recordFileData.createTime)),
                    modifier = Modifier.weight(1f),
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )
                Icon(
                    painter = painterResource(deletePainterRes),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clickable {
                            handler.onIntent(IFileControlIntent.Delete(recordFileData))
                        })
            }
        }
    }
}