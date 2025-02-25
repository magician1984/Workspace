package idv.bruce.camera_native.core.configure

import idv.bruce.camera_native.domain.entity.PreviewMode

object AVMConfigure {
    object CameraModeCode{
        const val AVM = -2
        const val GRID = -1
        const val CAM_0 = 0
        const val CAM_1 = 1
        const val CAM_2 = 1
        const val CAM_3 = 1
    }

    object ErrorType{
        const val TYPE_RENDERER = 0
        const val TYPE_CAMERA = 1
    }

    val defaultPreviewMode : PreviewMode = PreviewMode.Single(CameraModeCode.CAM_0)

    val activatePreviewModes : List<PreviewMode> = listOf(
        PreviewMode.Single(0)
    )
}