package idv.bruce.camera_native.domain.datasource

import android.view.Surface

interface IAVMSource {
    fun interface OnStatusChangeListener {
        fun onChange(mode: Int?, errorType: Int?, errorMsg: String?)
    }

    fun attachSurface(surface: Surface)
    fun detachSurface()
    fun switchMode(mode: Int)
    fun registerOnStatusChangeListener(listener: OnStatusChangeListener)
}