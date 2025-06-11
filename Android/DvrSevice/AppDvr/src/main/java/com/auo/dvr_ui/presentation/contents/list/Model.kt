package com.auo.dvr_ui.presentation.contents.list

import android.util.Log
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.presentation.GlobalState
import com.auo.dvr_ui.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class Model(
    scope: CoroutineScope,
    globalState: StateFlow<GlobalState>,
    private val onUpdateRecords: (Set<RecordType>)->Unit,
    private val lockGroups: (List<RecordGroup>) -> Unit,
    private val unlockGroups: (List<RecordGroup>) -> Unit,
    private val deleteGroups: (List<RecordGroup>) -> Unit,
    private val onReplayRequest: (RecordGroup) -> Unit
) : Presenter.IModel<UiState, UserIntent, Effect>(scope, globalState) {

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

    init {
        scope.launch {
            globalState
                .map { it.records }
                .distinctUntilChanged()
                .collect { records ->
                    _state.update { uiState -> uiState.copy(groupList = records) }
                }

            globalState.map { it.filterType }.distinctUntilChanged().collect {
                if(it.isEmpty())
                    return@collect
                val displayType = when(it.toList()[0]){
                    RecordType.Normal -> UiState.DisplayType.Normal
                    RecordType.Protected -> UiState.DisplayType.Incident
                    RecordType.Locked -> UiState.DisplayType.Locked
                    else -> UiState.DisplayType.Normal
                }
                _state.update { uiState -> uiState.copy(displayType = displayType) }
            }
        }
    }

    override fun handleUserIntent(intent: UserIntent) {
        scope.launch {
            Log.d("ListModel", "Intent: $intent")
            when(intent){
                UserIntent.Delete ->{
                    _effect.update { null }
                    deleteGroups(_state.value.selectedGroups)
                }
                is UserIntent.DisplayTypeChanged ->{
                    _state.value = _state.value.copy(displayType = intent.type)
                    updateGroups()
                }
                is UserIntent.ItemClicked -> onItemClicked(intent.item)
                UserIntent.Lock -> lockGroups(_state.value.selectedGroups)
                is UserIntent.SelectModeChanged -> {
                    _state.value = _state.value.copy(selectMode = intent.selectMode, selectedGroups = emptyList())
                }
                UserIntent.Unlock -> unlockGroups(_state.value.selectedGroups)

                UserIntent.SelectAll -> {
                    val groups  = _state.value.groupList
                    _state.value = _state.value.copy(selectedGroups = groups)
                }

                UserIntent.DeleteRequest -> {
                    _effect.update { Effect.ConfirmDelete }
                }

                UserIntent.CancelDelete ->{
                    _effect.update { null }
                }
            }
        }
    }

    private fun updateGroups(){
        val filter = when(_state.value.displayType){
            UiState.DisplayType.Normal -> setOf(RecordType.Normal)
            UiState.DisplayType.Incident -> setOf(RecordType.Protected)
            UiState.DisplayType.Locked -> setOf(RecordType.Locked)
        }

        onUpdateRecords(filter)
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
            onReplayRequest(item)
        }
    }
}