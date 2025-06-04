package com.auo.dvr_ui.presentation.contents.replay

import androidx.compose.runtime.mutableStateOf
import androidx.media3.exoplayer.ExoPlayer
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
    navController: NavHostController
) : Presenter.IModel<UiState, UserIntent, Effect>(scope, navController) {
    private val _state : MutableStateFlow<UiState> = MutableStateFlow(UiState(
        focusLocation = null,
        isPlaying = false,
        recordGroup = null
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
                is UserIntent.SurfaceReady -> onSurfaceReady()
                UserIntent.Pause -> TODO()
                is UserIntent.SeekTo -> TODO()
                is UserIntent.SelectCamera -> TODO()
                UserIntent.Start -> TODO()
                UserIntent.Stop -> TODO()
                UserIntent.Back -> onBack()
            }
        }
    }

    private fun onSurfaceReady(){
        readData()


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