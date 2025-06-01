package com.auo.dvr_ui.presentation.contents.list

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.auo.dvr_core.RecordGroup
import com.auo.dvr_ui.entity.IUseCaseDeleteGroups
import com.auo.dvr_ui.entity.IUseCaseGetRecordGroups
import com.auo.dvr_ui.entity.IUseCaseLockGroups
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseUnlockGroups
import com.auo.dvr_ui.entity.IUseCaseUnmountStorage
import com.auo.dvr_ui.presentation.Presenter
import com.auo.dvr_ui.usecase.UseCaseGetDvrState
import kotlinx.coroutines.CoroutineScope

class Model(
    private val onReplayRequest: (RecordGroup) -> Unit,
    override val scope: CoroutineScope,
    private val useCaseGetRecordGroups: IUseCaseGetRecordGroups,
    private val useCaseRegisterListener: IUseCaseRegisterListener,
    private val useCaseGetDvrState: UseCaseGetDvrState,
    private val useCaseLockGroups: IUseCaseLockGroups,
    private val useCaseUnlockGroups: IUseCaseUnlockGroups,
    private val useCaseDeleteGroups: IUseCaseDeleteGroups,
    private val useCaseUnmountStorage: IUseCaseUnmountStorage
) : Presenter.IModel<UiState, UserIntent, Effect> {
    private val _state: State<UiState> = mutableStateOf(UiState(
        groupList = emptyList(),
        selectedGroups = emptyList(),
        selectMode = false,
        displayType = UiState.DisplayType.All
    ))

    private val _effect: MutableState<Effect?> = mutableStateOf(null)

    override val state: State<UiState>
        get() = _state
    override val effect: State<Effect?>
        get() = _effect

    override fun handleUserIntent(intent: State<UserIntent>) {
        TODO("Not yet implemented")
    }

}