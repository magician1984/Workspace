package idv.bruce.camera_native.domain.usecase.impl

import android.view.Surface
import idv.bruce.camera_native.domain.datasource.IAVMSource
import idv.bruce.camera_native.domain.usecase.IUseCaseStartPreview

class UseCaseStartPreview(private val source : IAVMSource) : IUseCaseStartPreview {
    override fun invoke(surface: Surface) = source.attachSurface(surface)
}