package com.auo.dvr_ui.presentation.contents.list

import android.util.Log
import androidx.navigation.NavHostController
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class Model(
    scope: CoroutineScope,
    navController: NavHostController,
    private val getRecordGroups: (Set<RecordType>) -> List<RecordGroup>,
    private val registerListener: (onRecordGroupUpdate: () -> Unit) -> Unit,
    private val lockGroups: (List<RecordGroup>) -> Unit,
    private val unlockGroups: (List<RecordGroup>) -> Unit,
    private val deleteGroups: (List<RecordGroup>) -> Unit,
) : Presenter.IModel<UiState, UserIntent, Effect>(scope, navController) {

    private val _state : MutableStateFlow<UiState> = MutableStateFlow(
        UiState(
            groupList = emptyList(),
            selectedGroups = emptyList(),
            selectMode = false,
            displayType = UiState.DisplayType.Normal
        )
    )

    private val _effect : MutableStateFlow<Effect?> = MutableStateFlow(null)

    override val state: StateFlow<UiState>
        get() = _state
    override val effect: StateFlow<Effect?>
        get() = _effect

    override fun handleUserIntent(intent: UserIntent) {
        scope.launch {
            Log.d("ListModel", "Intent: $intent")
            when(intent){
                UserIntent.Delete -> deleteGroups(_state.value.selectedGroups)
                is UserIntent.DisplayTypeChanged ->{
                    _state.value = _state.value.copy(displayType = intent.type)
                    updateGroups()
                }
                is UserIntent.ItemClicked -> onItemClicked(intent.item)
                UserIntent.Lock -> lockGroups(_state.value.selectedGroups)
                is UserIntent.SelectModeChanged -> {
                    _state.value = _state.value.copy(selectMode = intent.selectMode)
                }
                UserIntent.Unlock -> unlockGroups(_state.value.selectedGroups)
                UserIntent.Init -> {
                    registerListener(::updateGroups)
                    updateGroups()
                }

                UserIntent.SelectAll -> {
                    val groups  = _state.value.groupList
                    _state.value = _state.value.copy(selectedGroups = groups)
                }
            }
        }
    }

    private fun updateGroups(){
        val filter = when(_state.value.displayType){
            UiState.DisplayType.Normal -> setOf(RecordType.Normal, RecordType.Locked)
            UiState.DisplayType.Incident -> setOf(RecordType.Protected)
            UiState.DisplayType.Locked -> setOf(RecordType.Locked)
        }

        val list = getRecordGroups(filter)

        _state.value = _state.value.copy(
            groupList = list,
            selectedGroups = emptyList()
        )
    }

    private fun onItemClicked(item: RecordGroup){
        val selectMode = _state.value.selectMode
        if(selectMode){
            val selected = _state.value.selectedGroups.toMutableList()
            if(selected.contains(item)){
                selected.remove(item)
            }else {
                selected.add(item)
            }
            _state.value = _state.value.copy(selectedGroups = selected)
        }else{
            navController.currentBackStackEntry?.savedStateHandle?.set("record", item)
            navController.navigate(Presenter.Screen.Replay.route)
        }
    }
}