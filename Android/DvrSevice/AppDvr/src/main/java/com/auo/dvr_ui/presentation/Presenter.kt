package com.auo.dvr_ui.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.Modifier
import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.usecase.IPresenter

class Presenter(
    private val composeRendererFunction : (parent: CompositionContext?, content: @Composable () -> Unit) -> Unit,
    override val useCaseGetListFiles: IUseCaseGetListFiles,
    override val useCaseRegisterListener: IUseCaseRegisterListener,
    override val useCaseLockFile: IUseCaseLockFile,
    override val useCaseUnlockFile: IUseCaseLockFile,
    override val useCaseDeleteFile: IUseCaseDeleteFile,
    override val useCaseGetCacheFile: IUseCaseGetCacheFile
) : IPresenter {

    internal interface IModel

    internal interface IView{
        @Composable
        fun Draw(modifier: Modifier)
    }

    override fun onReady() {
        TODO("Not yet implemented")
    }

    override fun onError(errMsg: String) {
        TODO("Not yet implemented")
    }

    override fun onLoading() {
        renderer {
            
        }
    }

    private fun renderer(content: @Composable () -> Unit){
        composeRendererFunction(null, content)
    }
}