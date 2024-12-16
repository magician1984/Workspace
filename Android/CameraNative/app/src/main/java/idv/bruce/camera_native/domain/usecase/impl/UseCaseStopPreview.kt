package idv.bruce.camera_native.domain.usecase.impl

import idv.bruce.camera_native.domain.datasource.IAVMSource
import idv.bruce.camera_native.domain.usecase.IUseCaseStopPreview

class UseCaseStopPreview(private val source : IAVMSource) : IUseCaseStopPreview {
    override fun invoke() = source.detachSurface()
}