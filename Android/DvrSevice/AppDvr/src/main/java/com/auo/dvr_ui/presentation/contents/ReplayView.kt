package com.auo.dvr_ui.presentation.contents

import android.net.Uri
import android.util.Log
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.IUserIntents
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class ReplayView(override val onIntent: (IUserIntents) -> Unit) : Presenter.IView {
    companion object {
        private const val TIME_FORMAT = "mm:ss"
        private const val NORMAL_DURATION_MILLI: Long = 5 * 60 * 1000
        private const val EVENT_DURATION_MILLI: Long = 90 * 1000
    }

    private sealed class ControlIntent {
        data object Play : ControlIntent()
        data object Pause : ControlIntent()
        data object Stop : ControlIntent()
        data class SeekTo(val value: Long) : ControlIntent()
    }

    private data class ReplayState(
        val isEnable: Boolean,
        val isPlaying: Boolean,
        val isPause: Boolean,
        val isLoading: Boolean,
        val duration: Long,
        val playingFile: File?
    )

    private val _mReplayState = mutableStateOf(
        ReplayState(
            isEnable = false,
            isPlaying = false,
            isPause = false,
            isLoading = false,
            0L,
            null
        )
    )

    private var mReplayState by _mReplayState

    private var mPlayer: Player? = null

    private val dateFormat: SimpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    @Composable
    override fun Draw(
        modifier: Modifier,
        state: Presenter.State
    ) {
        val mRecord = state.selectedFile

        LaunchedEffect(key1 = mRecord) {
            if (mRecord != null) {
                if (!mReplayState.isEnable)
                    mReplayState = mReplayState.copy(isEnable = true)

                if (mReplayState.playingFile != mRecord.cacheFile) {
                    mReplayState = mReplayState.copy(
                        playingFile = mRecord.cacheFile,
                        isPause = false,
                        duration = if (mRecord.type == RecordType.Protected) EVENT_DURATION_MILLI else NORMAL_DURATION_MILLI
                    )
                    if (mReplayState.isLoading)
                        handleIntent(ControlIntent.Play)
                    else
                        handleIntent(ControlIntent.Stop)
                }
            } else {
                mReplayState = mReplayState.copy(
                    isEnable = false,
                    isLoading = false,
                    isPause = false,
                    duration = 0L,
                    playingFile = null
                )
                handleIntent(ControlIntent.Stop)
            }
        }

        Column(modifier = modifier.padding(8.dp)) {
            VideoView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(color = Color.Blue)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ControlBar(
                modifier = Modifier.weight(1f),
                state = mReplayState
            ) {
                handleIntent(it)
            }
        }
    }

    @Composable
    private fun VideoView(modifier: Modifier) {
        AndroidView(modifier = modifier, factory = { context ->
            SurfaceView(context).apply {
                val view = this
                mPlayer = ExoPlayer.Builder(context).build()
                mPlayer!!.setVideoSurfaceView(this)
                mPlayer!!.addListener(object : Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        Log.d("ReplayView", "onIsPlayingChanged: $isPlaying")
                        mReplayState = mReplayState.copy(isPlaying = isPlaying)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        Log.d("ReplayView", "onPlaybackStateChanged: $playbackState")
                        if(playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED){
                            mReplayState = mReplayState.copy(isPlaying = false, isPause = false)
                        }

                    }
                })
            }
        })
    }

    @Composable
    private fun ControlBar(
        modifier: Modifier,
        state: ReplayState,
        onEvent: (ControlIntent) -> Unit
    ) {
        Column(
            modifier = modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimeInfo(isPlaying = state.isPlaying, duration = state.duration, onEvent = onEvent)
            Buttons(isEnable = state.isEnable, isPlaying = state.isPlaying, onEvent = onEvent)
        }
    }

    @Composable
    private fun TimeInfo(
        modifier: Modifier = Modifier,
        isPlaying: Boolean,
        duration: Long,
        onEvent: (ControlIntent) -> Unit
    ) {
        var currentTime by remember {
            mutableLongStateOf(0L)
        }


        LaunchedEffect(key1 = isPlaying) {
            while (isPlaying) {
                delay(300)
                currentTime = mPlayer?.currentPosition ?: 0L
            }
            if (!mReplayState.isPause)
                currentTime = 0L
        }

        LaunchedEffect(key1 = mPlayer?.playbackState) {
            if (mPlayer?.playbackState == Player.STATE_ENDED)
                currentTime = 0L
        }


        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(modifier = Modifier, text = dateFormat.format(Date(currentTime)), fontSize = 32.sp)
            Slider(
                modifier = Modifier,
                value = currentTime.toFloat(),
                valueRange = 0f..duration.toFloat().coerceAtLeast(0f),
                onValueChange = {
                    onEvent(ControlIntent.SeekTo(it.toLong()))
                })
        }

    }

    @Composable
    private fun Buttons(
        modifier: Modifier = Modifier,
        isEnable: Boolean,
        isPlaying: Boolean,
        onEvent: (ControlIntent) -> Unit
    ) {
        val playIconRes = remember { R.drawable.baseline_play_arrow_24 }
        val pauseIconRes = remember { R.drawable.baseline_pause_24 }
        val stopIconRes = remember { R.drawable.baseline_stop_24 }

        Row(modifier = modifier) {
            Icon(
                painter = painterResource(id = if (isPlaying) pauseIconRes else playIconRes),
                contentDescription = "",
                modifier = Modifier
                    .size(72.dp)
                    .clickable(enabled = isEnable) {
                        if (isPlaying) {
                            onEvent(ControlIntent.Pause)
                        } else {
                            onEvent(ControlIntent.Play)
                        }
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = stopIconRes),
                contentDescription = "",
                modifier = Modifier
                    .size(72.dp)
                    .clickable(enabled = isEnable) {
                        onEvent(ControlIntent.Stop)
                    })
        }
    }

    private fun handleIntent(intent: ControlIntent) {
        Log.d("ReplayView", "handleIntent: $intent")
        when (intent) {
            ControlIntent.Pause -> {
                mReplayState = mReplayState.copy(isPause = true)
                mPlayer?.pause()
            }

            ControlIntent.Play -> {
                if (mReplayState.isPause) {
                    mReplayState = mReplayState.copy(isPause = false)
                    mPlayer?.play()
                } else if (mReplayState.playingFile != null) {
                    mReplayState = mReplayState.copy(isLoading = false)
                    playFile(mPlayer!!, mReplayState.playingFile!!)
                } else {
                    mReplayState = mReplayState.copy(isLoading = true)
                    onIntent(IUserIntents.ReplayFile)
                }
            }

            is ControlIntent.SeekTo -> mPlayer?.seekTo(intent.value)
            ControlIntent.Stop -> {
                mPlayer?.stop()
            }
        }
    }

    private fun playFile(player: Player, file: File) {
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(file))
        player.setMediaItem(mediaItem)
        player.prepare()
        Log.d("ReplayView", "Duration : ${player.duration}")
        player.play()
    }
}