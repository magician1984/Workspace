package com.auo.dvr_ui.framework.exo

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.auo.dvr_ui.framework.ISyncVideoPlayer


@UnstableApi
class ExoVideoPlayer<T : Any>(
    private val mContext: Context,
    override val tag: T,
    looper: Looper = Looper.getMainLooper(),
    playbackLooper : Looper
) : ISyncVideoPlayer<T> {

    companion object {
        private val allocator: DefaultAllocator =
            DefaultAllocator(false, C.DEFAULT_BUFFER_SEGMENT_SIZE)


    }

    private val loadControl: DefaultLoadControl =
        DefaultLoadControl.Builder()
            .setAllocator(allocator)
            .setBufferDurationsMs(3000, 3000, 1000, 3000)
            .setPrioritizeTimeOverSizeThresholds(false)
            .setTargetBufferBytes(C.LENGTH_UNSET)
            .build()

    private val mExoPlayer: ExoPlayer =
        ExoPlayer.Builder(mContext)
            .setLooper(looper)
            .setPlaybackLooper(playbackLooper)
            .setLoadControl(loadControl)
            .build()
    private var mSurfaceHolder: SurfaceHolder? = null


    init {

        mExoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> Log.d("Exo", "Buffering")
                    Player.STATE_READY -> Log.d("Exo", "Ready to play")
                    Player.STATE_ENDED -> Log.d("Exo", "Ended")
                    Player.STATE_IDLE -> Log.d("Exo", "Idle")
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d("Exo", "Playing: $isPlaying")
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("Exo", "Error: ${error.message}")
            }
        })
    }

    override fun setView(view: SurfaceHolder) {
        mSurfaceHolder = view
        mExoPlayer.setVideoSurfaceHolder(view)
    }

    override fun clearView() {
        mExoPlayer.clearVideoSurfaceHolder(mSurfaceHolder)
    }

    @OptIn(UnstableApi::class)
    override fun prepare(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        val dataSourceFactory = DefaultDataSource.Factory(mContext)
        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        mExoPlayer.setMediaSource(mediaSource)
        mExoPlayer.prepare()
    }

    override fun play() {
        mExoPlayer.play()
    }

    override fun pause() {
        mExoPlayer.pause()
    }

    override fun stop() {
        mExoPlayer.stop()
    }

    override fun release() {
        mExoPlayer.release()
    }

    override val isPlaying: Boolean
        get() = mExoPlayer.isPlaying

    override val duration: Long
        get() = mExoPlayer.duration

    override val currentPosition: Long
        get() = mExoPlayer.currentPosition
}