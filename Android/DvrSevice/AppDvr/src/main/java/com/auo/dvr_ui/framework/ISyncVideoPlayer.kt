package com.auo.dvr_ui.framework

import android.net.Uri
import android.view.SurfaceHolder

interface ISyncVideoPlayer<T : Any> {
    fun setView(view : SurfaceHolder)
    fun clearView()

    fun prepare(uri : Uri)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun reset()

    val tag : T
    val isPlaying : Boolean
    val duration : Long
    val currentPosition : Long
}