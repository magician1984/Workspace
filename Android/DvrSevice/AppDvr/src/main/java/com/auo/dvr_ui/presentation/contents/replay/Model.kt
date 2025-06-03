package com.auo.dvr_ui.presentation.contents.replay

import androidx.navigation.NavHostController
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.entity.IUseCaseGetRecordGroups
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class Model(
    scope: CoroutineScope,
    navController: NavHostController,
    private val useCaseGetRecordGroups: IUseCaseGetRecordGroups
) : Presenter.IModel<UiState, UserIntent, Effect>(scope, navController) {
    override val state: StateFlow<UiState>
        get() = TODO("Not yet implemented")
    override val effect: StateFlow<Effect?>
        get() = TODO("Not yet implemented")

    override fun handleUserIntent(intent: UserIntent) {
        scope.launch {
            when(intent){
                UserIntent.Init -> onInit()
                UserIntent.Pause -> TODO()
                is UserIntent.SeekTo -> TODO()
                is UserIntent.SelectCamera -> TODO()
                UserIntent.Start -> TODO()
                UserIntent.Stop -> TODO()
            }
        }
    }

    private fun onInit(){
        val recordGroups : RecordGroup = navController.previousBackStackEntry?.savedStateHandle?.get<RecordGroup>("record") ?: return


    }
}