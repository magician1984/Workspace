package com.auo.performancetester.presentation.control

import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.FileAllocateMode
import com.auo.performancetester.domain.entity.FileSize
import com.auo.performancetester.presentation.UiState
import com.auo.performancetester.presentation.UserIntent

sealed class Item(open val name: String) {
    class MethodItem(val method: CloneMethod) : Item(method.name)
    class ModeItem(val mode : FileAllocateMode) : Item(mode.name)
    class SizeItem(val size: FileSize) : Item(size.name)
    class CountItem(val count: Int) : Item(count.toString())
}

sealed class ControlPageIntent : UserIntent {
    data object FinishApp : ControlPageIntent()
    class SelectMethod(val index : Int) : ControlPageIntent()
    class SelectSize(val index : Int) : ControlPageIntent()
    class SelectCount(val index : Int) : ControlPageIntent()
    class SelectMode(val index : Int) : ControlPageIntent()
    data object ExecuteTest : ControlPageIntent()
}

data class SpinnerParam(val items: List<Item>, val selectedIndex: Int)

data class ControlPageState(
    val inProgress: Boolean,
    val methodSpinner: SpinnerParam,
    val modeSpinner: SpinnerParam,
    val sizeSpinner: SpinnerParam,
    val countSpinner: SpinnerParam) :
    UiState