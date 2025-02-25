package com.auo.performancetester.presentation.info

import com.auo.performancetester.domain.entity.IData
import com.auo.performancetester.domain.usecase.IUseCaseInitialize
import com.auo.performancetester.domain.usecase.IUseCaseListenEvents
import com.auo.performancetester.presentation.IModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InfoModel(private val listenEvents: IUseCaseListenEvents, private val initialize: IUseCaseInitialize) : IModel<InfoIntent, InfoState> {
    override val state: StateFlow<InfoState>
        get() = _state

    private val _state : MutableStateFlow<InfoState> = MutableStateFlow(InfoState(emptyList(), emptyList()))

    override fun handleIntent(intent: InfoIntent) {
        when(intent){
            is InfoIntent.PagePrepare ->{
                listenEvents(::onEvent)
                initialize()
            }
        }
    }

    private fun onEvent(data : IData){
        when(data){
            is IData.EventMessage -> updateMessage(data)
            is IData.TestResult -> updateResult(data)
        }
    }

    private fun updateMessage(message : IData.EventMessage){
        _state.update { currentState ->
            currentState.copy(messages = currentState.messages + message)
        }
    }

    private fun updateResult(result : IData.TestResult){
        _state.update { currentState ->
            currentState.copy(results = currentState.results + result)
        }
    }
}