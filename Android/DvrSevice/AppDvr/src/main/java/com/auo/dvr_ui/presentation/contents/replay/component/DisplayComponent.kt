package com.auo.dvr_ui.presentation.contents.replay.component

import android.graphics.SurfaceTexture
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.contents.CommonComponents
import com.auo.dvr_ui.presentation.contents.replay.UiState

object DisplayComponent {
    data class ControlLayerConfig(
        val progressHigh: Dp,
        val progressBackgroundColor: Color,
        val progressTrackColor: Color,
        val mediaBtnSpace: Dp,
        val btnLayerPadding: PaddingValues
    )

    @Composable
    fun DisplayLayer(
        modifier: Modifier,
        state: UiState,
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
            if (holdersMap.size == camLocations.size) {
                val list = buildList {
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
                        .alpha(if (visible) 1f else 0f)
                        .clickable {
                            selected = if (selected == cam) null else cam
                        },
                    camLocation = cam,
                    isFocus = isFullScreen,
                    thumbnail = state.thumbnails[cam],
                    showThumbnail = state.showThumbnail,
                    onReady = { location, view ->
                        holdersMap[location] = view
                    },
                    onDetach = {}
                )
            }
        }
    }

    @Composable
    fun ControlLayer(
        modifier: Modifier,
        isPlaying: Boolean,
        time: Long,
        config: ControlLayerConfig,
        onPlayStateSwitch: () -> Unit,
        onPrevious: () -> Unit,
        onNext: () -> Unit,
        onBack: () -> Unit,
    ) {
        val mProgress by remember(time) {
            mutableFloatStateOf(time.toFloat() / 60000.toFloat())
        }
        Column(modifier = modifier) {
            ProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.progressHigh),
                backgroundColor = config.progressBackgroundColor,
                progressColor = config.progressTrackColor,
                progress = mProgress
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(config.btnLayerPadding)
            ) {
                CommonComponents.VectorButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    iconRes = R.drawable.btn_back,
                    touchedRes = R.drawable.btn_back_pressed,
                    onClick = onBack
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(config.mediaBtnSpace),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CommonComponents.VectorButton(
                        modifier = Modifier,
                        iconRes = R.drawable.btn_media_prev,
                        onClick = onPrevious
                    )
                    CommonComponents.VectorButton(
                        modifier = Modifier,
                        iconRes = if (isPlaying) R.drawable.btn_pause else R.drawable.btn_play,
                        onClick = onPlayStateSwitch
                    )
                    CommonComponents.VectorButton(
                        modifier = Modifier,
                        iconRes = R.drawable.btn_media_next,
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
        isFocus: Boolean,
        showThumbnail: Boolean,
        thumbnail: Uri?,
        onReady: (CamLocation, SurfaceHolder) -> Unit,
        onDetach: (CamLocation) -> Unit,
    ) {
        val mShowThumbnail by remember(showThumbnail) {
            mutableStateOf(showThumbnail)
        }

        val mContext = LocalContext.current

        val mColor1 = remember {
            Color(mContext.getColor(R.color.color_primary_1))
        }

        val mColor2 = remember {
            Color(mContext.getColor(R.color.color_primary_2))
        }

        val mIsFocus by remember(isFocus) {
            mutableStateOf(isFocus)
        }

        val labelBrush: Modifier = remember {
            Modifier.background(
                brush = Brush.verticalGradient(listOf(mColor2, mColor1)),
                shape = RoundedCornerShape(50)
            )
        }



        Box(modifier = modifier) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    SurfaceView(context).apply {
                        holder.addCallback(object : SurfaceHolder.Callback {
                            override fun surfaceCreated(holder: SurfaceHolder) {
                                onReady(camLocation, holder)
                            }

                            override fun surfaceChanged(
                                p0: SurfaceHolder,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {
                                Log.d("SurfaceView", "surfaceChanged: $p1, $p2, $p3")
                            }

                            override fun surfaceDestroyed(p0: SurfaceHolder) {
                                onDetach(camLocation)
                            }
                        })
                    }
                })

            if (mShowThumbnail) {
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnail)
                    .build()

                AsyncImage(
                    model = model,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }

            Box(
                modifier = Modifier
                    .size(229.dp, 80.dp)
                    .align(Alignment.TopStart)
                    .offset(46.dp, 33.dp)
                    .background(Color(0xB2FFFFFF), shape = RoundedCornerShape(50))
                    .then(if (mIsFocus) labelBrush else Modifier)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = camLocation.name,
                    fontSize = 35.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }

        }
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