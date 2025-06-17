package com.auo.dvr

interface IRemoteConnector {
    fun interface OnStateUpdateListener{
        fun onAvailable(isAvailable : Boolean)
    }

    fun addOnStateUpdateListener(listener : OnStateUpdateListener)

    fun removeOnStateUpdateListener(listener : OnStateUpdateListener)

    val isAvailable : Boolean
}