package com.auo.dvr_ui.framework.exo

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.SurfaceHolder
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.auo.dvr_ui.framework.ISyncVideoController

class ExoVideoController<T : Any> : ISyncVideoController<T, ExoVideoPlayer<T>> {
    override val isPlaying: Boolean
        get() = (mIsPlayingFlags and mFlagsMask) == mFlagsMask
    override val duration: Long
        get() = _duration
    override val currentPosition: Long
        get() = _currentPosition
    override val isReady: Boolean
        get() = (mReadyFlags and mFlagsMask) == mFlagsMask

    private val mPlayers = mutableMapOf<T, ExoVideoPlayer<T>>()

    private val mFlagMaskMap = mutableMapOf<T, Int>()

    private var mIsPlayingFlags : Int = 0
        set(value) {
            field = value
            if(onPlayStateUpdateListener != null){
                //Pre check all state is changed
                if(field == mFlagsMask || field == 0)
                    onPlayStateUpdateListener!!.onPlayStateChanged(isPlaying)
            }
        }

    private var mReadyFlags : Int = 0
        set(value) {
            field = value
            if(onReadyListener != null){
                if(field == mFlagsMask)
                    onReadyListener!!.onReady()
            }
        }

    private var mFlagsMask : Int = 0

    //Workaround
    private var _duration: Long = 60000L

    //Not implement yet
    private var _currentPosition: Long = 0L

    private val handlerThread = HandlerThread("exo-control").apply { start() }
    private val timerThread = HandlerThread("exo-timer").apply { start() }

    private val mHandler: Handler = Handler(handlerThread.looper)

    private val playbackThreadMap = mutableMapOf<T, HandlerThread>()

    private var onReadyListener: ISyncVideoController.OnReadyListener? = null
    private var onPositionUpdateListener: ISyncVideoController.OnPositionUpdateListener? = null
    private var onPlayStateUpdateListener: ISyncVideoController.OnPlayStateUpdate? = null

    private var timer: CustomTimer? = null

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

            timer = CustomTimer(durationMillis = 60000, intervalMillis = 1000, onTick = { position, duration->
                onPositionUpdateListener?.onUpdate(position, duration)
            }, onFinish = null, looper = timerThread.looper)
        }
    }

    @UnstableApi
    override fun play() {
        mHandler.post {
            Log.d("ExoVideoController", "play")
            mPlayers.values.forEach(ExoVideoPlayer<T>::play)
        }
    }

    @UnstableApi
    override fun pause() {
        mHandler.post {
            Log.d("ExoVideoController", "pause")
            mPlayers.values.forEach(ExoVideoPlayer<T>::pause)
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

    override fun addOnPlayStateUpdateListener(listener: ISyncVideoController.OnPlayStateUpdate) {
        onPlayStateUpdateListener = listener
    }

    override fun removeOnPlayStateUpdateListener(listener: ISyncVideoController.OnPlayStateUpdate) {
        onPlayStateUpdateListener = null
    }

    override fun addOnReadyListener(listener: ISyncVideoController.OnReadyListener) {
        onReadyListener = listener
    }

    override fun removeOnReadyListener(listener: ISyncVideoController.OnReadyListener) {
        onReadyListener = null
    }

    override fun addOnPositionUpdateListener(listener: ISyncVideoController.OnPositionUpdateListener) {
        onPositionUpdateListener = listener
    }

    override fun removeOnPositionUpdateListener(listener: ISyncVideoController.OnPositionUpdateListener) {
        onPositionUpdateListener = null
    }

    @UnstableApi
    override fun createPlayer(context: Context, tag: T) {
        if(mPlayers.containsKey(tag))
            return

        mHandler.post {
            val playbackThread = HandlerThread("exo-playback-$tag").apply { start() }

            playbackThreadMap[tag] = playbackThread

            ExoVideoPlayer(
                context,
                tag,
                looper = handlerThread.looper,
                playbackLooper = playbackThread.looper
            ).apply {
                mPlayers[tag] = this

                val offset = mFlagMaskMap.size

                mFlagMaskMap[tag] = offset

                mFlagsMask = mFlagsMask or (1 shl offset)

                mExoPlayer.addListener(object : Player.Listener{
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> Log.d("Exo", "[$tag] Buffering")
                            Player.STATE_READY ->{
                                Log.d("Exo", "[$tag] Ready to play")
                                mReadyFlags = mReadyFlags or (1 shl tag.offset())
                            }
                            Player.STATE_ENDED -> {
                                Log.d("Exo", "[$tag] Ended")
                                stop()
                            }
                            Player.STATE_IDLE -> {
                                Log.d("Exo", "[$tag] Idle")
                                mReadyFlags = mReadyFlags and (1 shl tag.offset()).inv()
                            }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Log.d("Exo", "[$tag] Playing: $isPlaying")
                        mIsPlayingFlags = if (isPlaying) {
                            mIsPlayingFlags or (1 shl tag.offset())
                        } else {
                            mIsPlayingFlags and (1 shl tag.offset()).inv()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("Exo", "[$tag] Error: ${error.message}")
                    }
                })
            }
        }
    }

    private fun T.offset() : Int{
        val offset : Int = mFlagMaskMap[this] ?: -1
        return offset
    }
}