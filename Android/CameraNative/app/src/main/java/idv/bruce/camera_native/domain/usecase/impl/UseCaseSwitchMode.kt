package idv.bruce.camera_native.domain.usecase.impl

import idv.bruce.camera_native.domain.datasource.IAVMSource
import idv.bruce.camera_native.domain.entity.PreviewMode
import idv.bruce.camera_native.domain.usecase.IUseCaseSwitchMode

class UseCaseSwitchMode(private val source : IAVMSource) : IUseCaseSwitchMode {
    override fun invoke(mode: PreviewMode) = source.switchMode(mode.modeInd)
}