package com.auo.dvr_ui.presentation.contents.replay.component

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.contents.CommonComponents
import com.auo.dvr_ui.ui.theme.DvrServiceTheme

object DisplayComponent {
    @Composable
    fun DisplayLayer(
        modifier: Modifier,
        onReady: (List<Pair<CamLocation, SurfaceHolder>>) -> Unit
    ) {
        val camLocations = remember {
            listOf(
                CamLocation.Front, CamLocation.Rear,
                CamLocation.Left, CamLocation.Right
            )
        }

        val holdersMap = remember { mutableStateMapOf<CamLocation, SurfaceHolder>() }
        var selected by remember { mutableStateOf<CamLocation?>(null) }

        LaunchedEffect(holdersMap) {
            if(holdersMap.size == camLocations.size){
                val list = buildList<Pair<CamLocation, SurfaceHolder>> {
                    holdersMap.entries.forEach { entry ->
                        this.add(entry.toPair())
                    }
                }
                onReady(list)
            }
        }

        if (holdersMap.size == camLocations.size) {
            // All ready: invoke once
            LaunchedEffect(Unit) {
                onReady(holdersMap.entries.map { it.toPair() })
            }
        }

        BoxWithConstraints(modifier = modifier) {
            val cellWidth = maxWidth / 2
            val cellHeight = maxHeight / 2

            camLocations.forEachIndexed { index, cam ->
                val row = index / 2
                val col = index % 2
                val isSelected = selected == cam
                val isFullScreen = selected != null

                val visible = selected == null || isSelected

                val itemModifier = if (!isFullScreen) {
                    Modifier
                        .offset(x = col * cellWidth, y = row * cellHeight)
                        .size(cellWidth, cellHeight)
                } else if (isSelected) {
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f)
                } else {
                    Modifier
                        .fillMaxSize()
                        .zIndex(0f)
                        .alpha(0f) // hide but retain
                }

                DisplayView(
                    modifier = itemModifier
                        .animateContentSize()
                        .clickable {
                            selected = if (selected == cam) null else cam
                        },
                    camLocation = cam,
                    isVisible = visible,
                    onReady = { loc, holder ->
                        holdersMap[loc] = holder
                    }
                )
            }
        }
    }

    @Composable
    fun ControlLayer(
        modifier: Modifier,
        sharedData: SharedData,
        backgroundColor: Color,
        highlightColor: Color,
        onPlayStateSwitch: () -> Unit,
        onPrevious: () -> Unit,
        onNext: () -> Unit,
        onBack: () -> Unit,
    ) {
        val mProgress by remember(sharedData.time) {
            mutableFloatStateOf(sharedData.time.toFloat() / 60000.toFloat())
        }
        Column(modifier = modifier) {
            ProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                backgroundColor = backgroundColor,
                progressColor = highlightColor,
                progress = mProgress
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                CommonComponents.CircleButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterStart),
                    iconRes = R.drawable.icon_back,
                    backgroundColor = Color.Transparent,
                    tintColor = Color.White,
                    onClick = onBack
                )

                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommonComponents.CircleButton(
                        modifier = Modifier,
                        iconRes = R.drawable.icon_prev,
                        backgroundColor = Color.Transparent,
                        tintColor = Color.White,
                        onClick = onPrevious
                    )
                    CommonComponents.CircleButton(
                        modifier = Modifier,
                        iconRes = R.drawable.icon_pause,
                        backgroundColor = Color.Gray,
                        tintColor = highlightColor,
                        onClick = onPlayStateSwitch
                    )
                    CommonComponents.CircleButton(
                        modifier = Modifier,
                        iconRes = R.drawable.icon_next,
                        backgroundColor = Color.Transparent,
                        tintColor = Color.White,
                        onClick = onNext
                    )
                }
            }
        }
    }

    @Composable
    private fun DisplayView(
        modifier: Modifier,
        camLocation: CamLocation,
        isVisible: Boolean,
        onReady: (CamLocation, SurfaceHolder) -> Unit
    ) {
        AndroidView(
            modifier = modifier.then(if (isVisible) Modifier else Modifier.alpha(0f)),
            factory = { context ->
                SurfaceView(context).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(p0: SurfaceHolder) {
                            onReady(camLocation, p0)
                        }

                        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                        }

                        override fun surfaceDestroyed(p0: SurfaceHolder) {
                        }
                    })
                }
            })
    }

    @Composable
    private fun ProgressBar(
        modifier: Modifier,
        backgroundColor: Color,
        progressColor: Color,
        progress: Float
    ) {
        androidx.compose.material3.LinearProgressIndicator(
            modifier = modifier,
            color = progressColor,
            trackColor = backgroundColor,
            progress = progress
        )
    }
}

@Preview
@Composable
private fun ControlLayerPreview() {
    DvrServiceTheme {
        DisplayComponent.ControlLayer(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            sharedData = SharedData(playState = SharedData.PlayState.Idle, time = 30000),
            backgroundColor = Color.Gray,
            highlightColor = MaterialTheme.colorScheme.primary,
            onPlayStateSwitch = {},
            onPrevious = {},
            onNext = {},
            onBack = {}
        )
    }
}