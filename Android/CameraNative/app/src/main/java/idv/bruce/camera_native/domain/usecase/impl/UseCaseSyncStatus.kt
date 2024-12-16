package idv.bruce.camera_native.domain.usecase.impl

import idv.bruce.camera_native.core.configure.AVMConfigure
import idv.bruce.camera_native.domain.datasource.IAVMSource
import idv.bruce.camera_native.domain.entity.AVMStatus
import idv.bruce.camera_native.domain.entity.ErrorType
import idv.bruce.camera_native.domain.entity.PreviewMode
import idv.bruce.camera_native.domain.usecase.IUseCaseSyncStatus

class UseCaseSyncStatus(private val source : IAVMSource) : IUseCaseSyncStatus {
    override fun invoke(callback: (AVMStatus) -> Unit) {
        source.registerOnStatusChangeListener{ mode: Int?, errType: Int?, errMsg: String? ->
            val status :AVMStatus = if(mode != null){
                when(mode){
                    AVMConfigure.CameraModeCode.GRID -> AVMStatus.Streaming(PreviewMode.Grid)
                    AVMConfigure.CameraModeCode.AVM -> AVMStatus.Streaming(PreviewMode.Avm)
                    else -> AVMStatus.Streaming(PreviewMode.Single(mode))
                }
            }else if(errType != null){
                val msg : String = errMsg?:"Unknown"
                val errorType : ErrorType = when(errType){
                    AVMConfigure.ErrorType.TYPE_RENDERER -> ErrorType.RendererError(msg)
                    AVMConfigure.ErrorType.TYPE_CAMERA -> ErrorType.CameraError(msg)
                    else-> return@registerOnStatusChangeListener
                }
                AVMStatus.Error(errorType)
            }else{
                AVMStatus.None
            }

            callback.invoke(status)
        }
    }
}