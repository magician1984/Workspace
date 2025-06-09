package com.auo.dvr

import java.io.File

internal interface IFileObserver {
    enum class EventType {
        Create,
        Close,
        Delete,
        Exist
    }

    fun interface OnEventCallback {
        fun onEvent(event: EventType, file: File)
    }

    fun start()
    fun stop()
    fun setOnEventCallback(callback: OnEventCallback)
}