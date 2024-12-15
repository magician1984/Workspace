package idv.bruce.camera_native.domain.datasource

import android.view.Surface

interface ICameraSource {
    data object CameraMode{
        const val AVM_MODE = -2
        const val GRID_MODE = -1
        const val CAMERA_1 = 0
        const val CAMERA_2 = 1
        const val CAMERA_3 = 2
        const val CAMERA_4 = 3
    }

    fun init()
    fun attachSurface(surface: Surface)
    fun detachSurface()
    fun switchMode(mode : Int)
}