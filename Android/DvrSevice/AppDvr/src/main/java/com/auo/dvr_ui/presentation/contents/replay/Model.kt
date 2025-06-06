package com.auo.dvr_ui.presentation.contents.replay

import android.net.Uri
import android.view.SurfaceHolder
import android.view.TextureView
import androidx.navigation.NavHostController
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class Model(
    scope: CoroutineScope,
    navController: NavHostController,
    private val onViewReady : (List<Pair<CamLocation, SurfaceHolder>>) -> Unit,
    private val onPrepareRequest : (RecordGroup) -> Unit,
    private val onPlayRequest : () -> Unit,
    private val onPauseRequest : () -> Unit,
    private val onNextRequest : () -> Unit,
    private val onPrevRequest : () -> Unit
) : Presenter.IModel<UiState, UserIntent, Effect>(scope, navController) {
    private val _state : MutableStateFlow<UiState> = MutableStateFlow(UiState(
        isPlaying = false,
        thumbnails = emptyMap(),
        showThumbnail = false
    ))

    private val _effect : MutableStateFlow<Effect?> = MutableStateFlow(null)

    override val state: StateFlow<UiState>
        get() = _state

    override val effect: StateFlow<Effect?>
        get() = _effect

    private var mCurrentRecordGroup : RecordGroup? = null

    private val mRecordGroups : MutableList<RecordGroup> = mutableListOf()

    override fun handleUserIntent(intent: UserIntent) {
        scope.launch {
            when(intent){
                is UserIntent.SurfaceReady -> onSurfaceReady(intent.list)
                is UserIntent.SeekTo -> TODO()
                is UserIntent.SelectCamera -> TODO()
                UserIntent.Back -> onBack()
                UserIntent.Next -> TODO()
                UserIntent.PlayStateSwitch ->{
                    if(_state.value.isPlaying){
                        onPauseRequest()
                        _state.value = _state.value.copy(isPlaying = false)
                    }else{
                        onPlayRequest()
                        _state.value = _state.value.copy(isPlaying = true, showThumbnail = false)
                    }
                }
                UserIntent.Previous -> TODO()
            }
        }
    }

    private fun onSurfaceReady(list : List<Pair<CamLocation, SurfaceHolder>>){
        readData()

        onViewReady(list)

        val thumbnails = mutableMapOf<CamLocation, Uri>()

        mCurrentRecordGroup?.files?.forEach{
            thumbnails[it.location] = it.thumbnail
        }?: return

        onPrepareRequest(mCurrentRecordGroup ?: return)

        _state.value = _state.value.copy(
            thumbnails = thumbnails,
            showThumbnail = true
        )
    }

    private fun readData(){
        mCurrentRecordGroup = navController.previousBackStackEntry?.savedStateHandle?.get<RecordGroup>("record")

        val list = navController.previousBackStackEntry?.savedStateHandle?.get<Array<RecordGroup>>("list")

        mRecordGroups.clear()

        mRecordGroups.addAll(list?.toList() ?: emptyList())
    }

    private fun onBack(){
        scope.launch(Dispatchers.Main) {
            navController.navigate(Presenter.Screen.List.route)
        }
    }
}