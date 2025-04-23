package com.auo.dvr_ui.presentation.contents

import android.net.Uri
import android.util.Log
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
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
        val duration: Long
    )

    private val _mReplayState = mutableStateOf(
        ReplayState(
            isEnable = false,
            isPlaying = false,
            isPause = false,
            isLoading = false,
            0L
        )
    )

    private var mReplayState by _mReplayState

    private val _mPlayerInitialized = mutableStateOf(false)

    private var mPlayerInitialized by _mPlayerInitialized

    private lateinit var mPlayer: Player

    private val dateFormat: SimpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    @Composable
    override fun Draw(
        modifier: Modifier,
        state: Presenter.State
    ) {

        val mContext = LocalContext.current
        // Init player
        LaunchedEffect(LocalContext.current) {
            mPlayer = ExoPlayer.Builder(mContext).build()

            mPlayer.addListener(object : Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    Log.d("ReplayView", "onIsPlayingChanged: $isPlaying")
                    updateReplayState(isPlaying = isPlaying, isPause = !isPlaying)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    Log.d("ReplayView", "onPlaybackStateChanged: $playbackState")
                    if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED) {
                        updateReplayState(isPlaying = false, isPause = false, duration = 0L)
                    }
                }
            })

            mPlayerInitialized = true
        }

        // Release player
        DisposableEffect(LocalContext.current) {
            onDispose {
                mPlayer.release()
                mPlayerInitialized = false
            }
        }

        LaunchedEffect(mPlayerInitialized, state) {
            if (mPlayerInitialized) {
                Log.d("ReplayView", "State: $state")
                val file = state.playingFile

                updateReplayState(isEnable = state.selectedFile != null, isLoading = file != null)

                if (file == null) {
                    stop()
                }else{
                    play(file)
                }
            }
        }

        Column(modifier = modifier.padding(8.dp)) {
            VideoView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(color = Color.Blue),
                replayState = mReplayState
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
    private fun VideoView(modifier: Modifier, replayState: ReplayState) {
        Box(modifier = modifier) {
            if(mPlayerInitialized){
                AndroidView(factory = { context ->
                    SurfaceView(context).apply {
                        mPlayer.setVideoSurfaceView(this)
                    }
                })
            }

            if(!replayState.isLoading){
                Surface(modifier = Modifier.fillMaxSize().background(Color.Black)) {  }
            }
        }

    }

    @Composable
    private fun ControlBar(
        modifier: Modifier,
        state: ReplayState,
        onEvent: (ControlIntent) -> Unit
    ) {

        var currentTime by remember {
            mutableLongStateOf(0L)
        }

        LaunchedEffect(key1 = state) {
            while (state.isPlaying) {
                delay(300)
                currentTime = mPlayer.currentPosition
            }

            if (!state.isLoading)
                currentTime = 0L
        }

        Column(
            modifier = modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(modifier = Modifier, text = dateFormat.format(Date(currentTime)), fontSize = 32.sp)
                Slider(
                    modifier = Modifier,
                    value = currentTime.toFloat(),
                    valueRange = 0f..state.duration.toFloat().coerceAtLeast(0f),
                    onValueChange = {
                        onEvent(ControlIntent.SeekTo(it.toLong()))
                    })
            }
            Buttons(isEnable = state.isEnable, isPlaying = state.isPlaying, onEvent = onEvent)
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
        Log.d("ReplayView", "handleIntent: $intent, ReplayState: $mReplayState")
        when (intent) {
            ControlIntent.Pause -> {
                pause()
            }

            ControlIntent.Play -> {
                if(mReplayState.isLoading){
                    resume()
                }else{
                    onIntent(IUserIntents.RequestPlayFile)
                }
            }

            is ControlIntent.SeekTo -> mPlayer.seekTo(intent.value)
            ControlIntent.Stop -> onIntent(IUserIntents.ReleasePlayFile)
        }
    }

    private fun play(file: File) {
        if(mPlayer.isPlaying)
            return
        Log.d("Player", "Play called")
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.fromFile(file))
        mPlayer.setMediaItem(mediaItem)
        mPlayer.prepare()
        Log.d("ReplayView", "Duration : ${mPlayer.duration}")
        mPlayer.play()
    }

    private fun pause(){
        Log.d("Player", "Pause called")
        mPlayer.pause()
    }

    private fun resume(){
        Log.d("Player", "Resume calles")
        mPlayer.play()
    }

    private fun stop(){
        Log.d("Player", "Stop called")
        mPlayer.stop()
    }

    private fun updateReplayState(
        isEnable: Boolean = mReplayState.isEnable,
        isPlaying: Boolean = mReplayState.isPlaying,
        isPause: Boolean = mReplayState.isPause,
        isLoading: Boolean = mReplayState.isLoading,
        duration: Long = mReplayState.duration
    ) {
        Log.d(
            "ReplayView",
            "Update state: $isEnable, $isPlaying, $isPause, $isLoading, $duration"
        )
        mReplayState = mReplayState.copy(
            isEnable = isEnable,
            isPlaying = isPlaying,
            isPause = isPause,
            isLoading = isLoading,
            duration = duration
        )
    }
}