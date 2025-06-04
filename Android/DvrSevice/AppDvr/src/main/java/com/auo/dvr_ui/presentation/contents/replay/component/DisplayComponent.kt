package com.auo.dvr_ui.presentation.contents.replay.component

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.view.SurfaceHolder
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordFile
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.contents.CommonComponents
import com.auo.dvr_ui.presentation.contents.replay.UiState
import com.auo.dvr_ui.ui.theme.DvrServiceTheme

object DisplayComponent {


    data class ControlLayerConfig(
        val progressHigh: Dp,
        val progressBackgroundColor: Color,
        val progressTrackColor : Color,
        val mediaBtnSpace: Dp,
        val btnLayerPadding : PaddingValues
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

        val mIsPlaying by remember(state) {
            mutableStateOf(state.isPlaying)
        }

        val mRecordGroup by remember(state) {
            mutableStateOf(state.recordGroup)
        }

        val holdersMap = remember { mutableStateMapOf<CamLocation, SurfaceHolder>() }
        var selected by remember(state) { mutableStateOf(state.focusLocation) }

        LaunchedEffect(holdersMap) {
            if (holdersMap.size == camLocations.size) {
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
                val record: RecordFile? = mRecordGroup?.files?.find { it.location == cam }
                DisplayView(
                    modifier = itemModifier
                        .animateContentSize()
                        .clickable {
                            selected = if (selected == cam) null else cam
                        },
                    camLocation = cam,
                    isFocus = visible,
                    isPlaying = mIsPlaying,
                    playItem = record?.uri,
                    thumbnail = record?.thumbnail,
                    onReady = {},
                    onSelect = {}
                )
            }
        }
    }

    @Composable
    fun ControlLayer(
        modifier: Modifier,
        isPlaying: Boolean,
        time: Long,
        config : ControlLayerConfig,
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
        isPlaying: Boolean,
        isFocus: Boolean,
        thumbnail: Uri?,
        playItem: Uri? = null,
        onReady: (CamLocation) -> Unit,
        onSelect: (CamLocation) -> Unit
    ) {

        val mContext: Context = LocalContext.current

        val mPlayer: ExoPlayer = remember {
            ExoPlayer.Builder(mContext).build()
        }

        var mShowThumbnail by remember {
            mutableStateOf(true)
        }

        LaunchedEffect(mPlayer.playbackState) {
            mShowThumbnail = if (mPlayer.playbackState != ExoPlayer.STATE_READY)
                true
            else
                false
        }

        LaunchedEffect(playItem) {
            if (playItem != null) {
                if (mPlayer.mediaItemCount > 0) {
                    mPlayer.stop()
                    mPlayer.clearMediaItems()
                }

                val mediaItem: MediaItem = MediaItem.fromUri(playItem)
                mPlayer.setMediaItem(mediaItem)
                mPlayer.prepare()
                onReady(camLocation)
            }
        }

        LaunchedEffect(isPlaying) {
            if (mPlayer.playbackState == ExoPlayer.STATE_IDLE)
                return@LaunchedEffect
            if (isPlaying)
                mPlayer.play()
            else
                mPlayer.pause()
        }

        Box(modifier = modifier.then(if (isFocus) Modifier else Modifier.alpha(0f))) {

            CommonComponents.RoundedButton(
                modifier = Modifier
                    .size(229.dp, 80.dp)
                    .align(Alignment.TopStart)
                    .offset(46.dp, 33.dp),
                label = camLocation.name,
                borderColor = Color.Transparent,
                borderWidth = 0.dp,
                textSize = 35.sp,
                backgroundColor = Color.Gray,
                textColor = Color.Black,
                onClick = { onSelect(camLocation) }
            )

            if (mShowThumbnail) {
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnail)
                    .build()

                AsyncImage(
                    model = model,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(0.5f),
                    contentDescription = null,
                )

            }

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0f), factory = { context ->
                    TextureView(context).apply {
                        surfaceTextureListener = object : SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(
                                p0: SurfaceTexture,
                                p1: Int,
                                p2: Int
                            ) {
                                mPlayer.setVideoTextureView(this@apply)
                            }

                            override fun onSurfaceTextureSizeChanged(
                                p0: SurfaceTexture,
                                p1: Int,
                                p2: Int
                            ) {
                            }

                            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                                mPlayer.clearVideoTextureView(this@apply)
                                return true
                            }

                            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                            }
                        }
                    }
                })
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

@Preview(widthDp = 1920, heightDp = 973)
@Composable
private fun ControlLayerPreview() {
    DvrServiceTheme {
        DisplayComponent.ControlLayer(
            modifier = Modifier
                .fillMaxWidth(),
            isPlaying = false,
            time = 0L,
            config = DisplayComponent.ControlLayerConfig(
                progressHigh = 8.dp,
                progressBackgroundColor = Color.Gray,
                progressTrackColor = Color.White,
                mediaBtnSpace = 88.dp,
                btnLayerPadding = PaddingValues(start = 46.dp, top = 30.dp, bottom = 26.dp)
            ),
            onPlayStateSwitch = {},
            onPrevious = {},
            onNext = {},
            onBack = {}
        )
    }
}