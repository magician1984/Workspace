package com.auo.performancetester.presentation.control

import android.util.Log
import com.auo.performancetester.domain.entity.CloneMethod
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
        val CloneMethodItems: List<Item.MethodItem> = listOf(
            Item.MethodItem(CloneMethod.BufferIO),
            Item.MethodItem(CloneMethod.FileChannel),
        )

        val SizeItems: List<Item.SizeItem> = listOf(
            Item.SizeItem(52428800),
            Item.SizeItem(104857600),
            Item.SizeItem(209715200),
        )

        val CountItems: List<Item.CountItem> = listOf(
            Item.CountItem(1),
            Item.CountItem(5),
            Item.CountItem(10),
            Item.CountItem(20)
        )
    }

    private val _state : MutableStateFlow<ControlPageState> = MutableStateFlow(ControlPageState(
        inProgress = false,
        methodSpinner = SpinnerParam(CloneMethodItems, 0),
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
                is ControlPageIntent.ExecuteTest -> execute()
                is ControlPageIntent.FinishApp -> exitUseCase()
                is ControlPageIntent.SelectCount -> updateCountSelect(intent.index)
                is ControlPageIntent.SelectMethod -> updateMethodSelect(intent.index)
                is ControlPageIntent.SelectSize -> updateSizeSelect(intent.index)
            }
        }
    }

    private fun execute(){
        Log.d("ControlModel", "Execute")
        val method = CloneMethodItems[state.value.methodSpinner.selectedIndex].method
        val size = SizeItems[state.value.sizeSpinner.selectedIndex].size
        val count = CountItems[state.value.countSpinner.selectedIndex].count

        testUseCase(method, size, count)
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
}