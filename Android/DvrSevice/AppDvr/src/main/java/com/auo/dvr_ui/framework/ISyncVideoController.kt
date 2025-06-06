package com.auo.dvr_ui.framework

import android.content.Context
import android.net.Uri
import android.view.SurfaceHolder

interface ISyncVideoController<T : Any, P : ISyncVideoPlayer<T>> {
    fun interface OnPlayStateUpdate {
        fun onPlayStateChanged(isPlaying: Boolean)
    }

    fun interface OnReadyListener {
        fun onReady()
    }

    fun interface OnPositionUpdateListener {
        fun onUpdate(position: Long, duration: Long)
    }

    fun setView(tag: T, view: SurfaceHolder)
    fun clearTextureView(tag: T)
    fun prepare(tag: T, uri: Uri)

    fun play()
    fun pause()
    fun stop()
    fun release()

    fun createPlayer(context: Context, tag: T)

    fun addOnPlayStateUpdateListener(listener: OnPlayStateUpdate)
    fun removeOnPlayStateUpdateListener(listener: OnPlayStateUpdate)

    fun addOnReadyListener(listener: OnReadyListener)
    fun removeOnReadyListener(listener: OnReadyListener)

    fun addOnPositionUpdateListener(listener: OnPositionUpdateListener)
    fun removeOnPositionUpdateListener(listener: OnPositionUpdateListener)

    val isPlaying: Boolean
    val duration: Long
    val currentPosition: Long
    val isReady: Boolean
}