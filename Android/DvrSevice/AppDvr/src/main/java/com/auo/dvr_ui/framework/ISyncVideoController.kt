package com.auo.dvr_ui.framework

import android.content.Context
import android.net.Uri
import android.view.SurfaceHolder

interface ISyncVideoController<T : Any, P : ISyncVideoPlayer<T>> {
    fun setView(tag : T, view : SurfaceHolder)
    fun clearTextureView(tag : T)
    fun prepare(tag : T, uri : Uri)

    fun play()
    fun pause()
    fun stop()
    fun release()

    fun createPlayer(context : Context, tag : T)

    val isPlaying : Boolean
    val duration : Long
    val currentPosition : Long
}