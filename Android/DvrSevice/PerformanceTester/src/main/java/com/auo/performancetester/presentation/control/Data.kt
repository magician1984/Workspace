package com.auo.performancetester.presentation.control

import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.presentation.UiState
import com.auo.performancetester.presentation.UserIntent

sealed class Item(open val name: String) {
    class MethodItem(val method: CloneMethod) : Item(method.name)
    class SizeItem(val size: Long) : Item("") {
        override val name: String
            get() = "${size.toMB()} MB"

        private fun Long.toMB(): Float = this / 1024f / 1024f
    }

    class CountItem(val count: Int) : Item(count.toString())
}

sealed class ControlPageIntent : UserIntent {
    data object FinishApp : ControlPageIntent()
    class SelectMethod(val index : Int) : ControlPageIntent()
    class SelectSize(val index : Int) : ControlPageIntent()
    class SelectCount(val index : Int) : ControlPageIntent()
    data object ExecuteTest : ControlPageIntent()
}

data class SpinnerParam(val items: List<Item>, val selectedIndex: Int)

data class ControlPageState(
    val inProgress: Boolean,
    val methodSpinner: SpinnerParam,
    val sizeSpinner: SpinnerParam,
    val countSpinner: SpinnerParam) :
    UiState