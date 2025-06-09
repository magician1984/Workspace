package com.auo.performancetester.presentation.control

import android.util.Log
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.FileAllocateMode
import com.auo.performancetester.domain.entity.FileSize
import com.auo.performancetester.domain.usecase.IUseCaseExit
import com.auo.performancetester.domain.usecase.IUseCaseStartTest
import com.auo.performancetester.presentation.IModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ControlModel(private val exitUseCase : IUseCaseExit, private val testUseCase : IUseCaseStartTest) : IModel<ControlPageIntent, ControlPageState> {
    private companion object{
        val CloneMethodItems: List<Item.MethodItem> = CloneMethod.entries.map { Item.MethodItem(it) }

        val SizeItems: List<Item.SizeItem> = FileSize.entries.map { Item.SizeItem(it) }

        val CountItems: List<Item.CountItem> = listOf(
            Item.CountItem(10),
            Item.CountItem(50),
            Item.CountItem(100),
            Item.CountItem(200)
        )

        val FileAllocateModeItems: List<Item.ModeItem> = FileAllocateMode.entries.map { Item.ModeItem(it) }
    }

    private val _state : MutableStateFlow<ControlPageState> = MutableStateFlow(ControlPageState(
        inProgress = false,
        methodSpinner = SpinnerParam(CloneMethodItems, 0),
        modeSpinner = SpinnerParam(FileAllocateModeItems, 0),
        sizeSpinner = SpinnerParam(SizeItems, 0),
        countSpinner = SpinnerParam(CountItems, 0)
    ))

    override val state: StateFlow<ControlPageState>
        get() = _state
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()

    override fun handleIntent(intent: ControlPageIntent) {
        launch {
            when(intent){
                is ControlPageIntent.ExecuteTest -> executeAll()
                is ControlPageIntent.FinishApp -> exitUseCase()
                is ControlPageIntent.SelectCount -> updateCountSelect(intent.index)
                is ControlPageIntent.SelectMethod -> updateMethodSelect(intent.index)
                is ControlPageIntent.SelectSize -> updateSizeSelect(intent.index)
                is ControlPageIntent.SelectMode -> updateModeSelect(intent.index)
            }
        }
    }

    private fun execute(){
        if(state.value.inProgress){
            Log.e("ControlModel", "Execute in progress")
            return
        }
        Log.d("ControlModel", "Execute")
        val method = CloneMethodItems[state.value.methodSpinner.selectedIndex].method
        val size = SizeItems[state.value.sizeSpinner.selectedIndex].size
        val count = CountItems[state.value.countSpinner.selectedIndex].count
        val mode = FileAllocateModeItems[state.value.modeSpinner.selectedIndex].mode


        _state.update {currentState->
            currentState.copy(inProgress = true)
        }
        testUseCase(method, mode, size, count, true)
        _state.update {currentState->
            currentState.copy(inProgress = false)
        }
    }

    private fun executeAll(){
        if(state.value.inProgress){
            Log.e("ControlModel", "Execute in progress")
            return
        }
        Log.d("ControlModel", "Execute all")
        _state.update {currentState->
            currentState.copy(inProgress = true)
        }
        val count = 20
        val cloneMethods = listOf(CloneMethod.FileChannel)
        val fileSize = listOf(FileSize.Large)
        val modes = listOf(FileAllocateMode.NonPreAllocate)
        val syncFlags = listOf(true)

        Log.d("ControlModel", "Start tests")
        cloneMethods.forEach { method ->
            modes.forEach { mode ->
                fileSize.forEach { size ->
                    syncFlags.forEach { isSync ->
                        Log.d("ControlModel", "Test case: $method, $mode, $size, $count, $isSync")
                        testUseCase(method, mode, size, count, isSync)
                    }
                }
            }
        }

        _state.update {currentState->
            currentState.copy(inProgress = false)
        }
        Log.d("ControlModel", "All test finished")
    }

    private fun updateMethodSelect(selectedIndex: Int){
        _state.update { currentState ->
            currentState.copy(methodSpinner = SpinnerParam(CloneMethodItems, selectedIndex))
        }
    }

    private fun updateSizeSelect(selectedIndex: Int){
        _state.update { currentState ->
            currentState.copy(sizeSpinner = SpinnerParam(SizeItems, selectedIndex))
        }
    }

    private fun updateCountSelect(selectedIndex: Int){
        _state.update { currentState ->
            currentState.copy(countSpinner = SpinnerParam(CountItems, selectedIndex))
        }
    }

    private fun updateModeSelect(selectedIndex: Int){
        _state.update { currentState ->
            currentState.copy(modeSpinner = SpinnerParam(FileAllocateModeItems, selectedIndex))
        }
    }
}