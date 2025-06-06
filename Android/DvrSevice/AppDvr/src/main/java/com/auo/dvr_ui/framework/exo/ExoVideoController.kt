package com.auo.dvr_ui.framework.exo

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.TextureView
import androidx.media3.common.util.UnstableApi
import com.auo.dvr_ui.framework.ISyncVideoController

class ExoVideoController<T : Any> : ISyncVideoController<T, ExoVideoPlayer<T>> {
    override val isPlaying: Boolean
        get() = _isPlaying
    override val duration: Long
        get() = _duration
    override val currentPosition: Long
        get() = _currentPosition

    private val mPlayers = mutableMapOf<T, ExoVideoPlayer<T>>()

    private var _isPlaying: Boolean = false

    //Workaround
    private var _duration: Long = 60000L

    //Not implement yet
    private var _currentPosition: Long = 0L

    val handlerThread = HandlerThread("exo-cotrol").apply { start() }

    private val mHandler: Handler = Handler(handlerThread.looper)

    private val playbackThreadMap = mutableMapOf<T, HandlerThread>()

    @UnstableApi
    override fun setView(tag: T, view: SurfaceHolder) {
        mHandler.post {
            Log.d("ExoVideoController", "setTextureView: $tag, ${view.isCreating}")
            mPlayers[tag]?.setView(view)
                ?: throw IllegalArgumentException("Player not found for tag: $tag")
        }
    }

    @UnstableApi
    override fun clearTextureView(tag: T) {
        mHandler.post {
            Log.d("ExoVideoController", "clearTextureView: $tag")
            mPlayers[tag]?.clearView()
                ?: throw IllegalArgumentException("Player not found for tag: $tag")
        }
    }

    @UnstableApi
    override fun prepare(tag: T, uri: Uri) {
        mHandler.post {
            mPlayers[tag]?.prepare(uri)
                ?: throw IllegalArgumentException("Player not found for tag: $tag")
        }
    }

    @UnstableApi
    override fun play() {
        mHandler.post {
            Log.d("ExoVideoController", "play")
            mPlayers.values.forEach(ExoVideoPlayer<T>::play)
            _isPlaying = true
        }
    }

    @UnstableApi
    override fun pause() {
        mHandler.post {
            Log.d("ExoVideoController", "pause")
            mPlayers.values.forEach(ExoVideoPlayer<T>::pause)
            _isPlaying = false
        }
    }

    @UnstableApi
    override fun stop() {
        mHandler.post {
            mPlayers.values.forEach(ExoVideoPlayer<T>::stop)
        }
    }

    @UnstableApi
    override fun release() {
        mHandler.post {
            mPlayers.values.forEach(ExoVideoPlayer<T>::release)
            playbackThreadMap.values.forEach{
                it.quitSafely()
                it.join()
            }
        }
    }

    @UnstableApi
    override fun createPlayer(context: Context, tag: T) {
        mHandler.post {
            val playbackThread = HandlerThread("exo-playback-$tag").apply { start() }

            playbackThreadMap[tag] = playbackThread

            mPlayers[tag] = ExoVideoPlayer(
                context,
                tag,
                looper = handlerThread.looper,
                playbackLooper = playbackThread.looper
            )
        }
    }
}