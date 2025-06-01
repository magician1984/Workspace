package com.auo.dvr_ui.presentation

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.auo.dvr_ui.entity.IUseCase
import com.auo.dvr_ui.ui.theme.DvrServiceTheme
import com.auo.dvr_ui.usecase.IPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import com.auo.dvr_ui.presentation.contents.list.View as ListView
import com.auo.dvr_ui.presentation.contents.list.Model as ListModel
import com.auo.dvr_ui.presentation.contents.replay.View as ReplayView
import com.auo.dvr_ui.presentation.contents.replay.Model as ReplayModel

private typealias ViewContent = @Composable (PaddingValues) -> Unit

class Presenter(
    private val renderer: ComponentActivity
) : IPresenter {
    internal interface IUserIntent

    internal interface IUiState

    internal interface IEffect

    internal interface IModel<S : IUiState, I : IUserIntent, E : IEffect> {
        val state: State<S>
        val effect: State<E?>
        val scope: CoroutineScope
        fun handleUserIntent(intent: State<I>)
    }

    internal interface IView<S : IUiState, I : IUserIntent, E : IEffect> {
        val userIntent: State<I>
        fun handleUiState(state: State<S>)
        fun handleEffect(effect: State<E?>)

        @Composable
        fun Draw(modifier: Modifier, arg : Any? = null)
    }

    private val useCaseList: MutableList<IUseCase> = mutableListOf()

    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    private val currentView: MutableState<ViewContent> =
        mutableStateOf({ innerPadding -> OnLoading(innerPadding) })

    override fun summitUseCases(vararg useCases: IUseCase) {
        useCaseList.clear()
        useCaseList.addAll(useCases)

        currentView.value = { innerPadding -> OnReady(innerPadding) }
    }

    override fun render() {
        renderer.setContent {
            DvrServiceTheme {
                Scaffold { innerPadding ->
                    currentView.value(innerPadding)
                }
            }
        }
    }

    @Composable
    private fun OnReady(innerPadding: PaddingValues) {
        try {
            val listView: ListView = ListView()
            val listModel: ListModel = ListModel(
                onReplayRequest = {},
                scope = backgroundScope,
                useCaseGetRecordGroups = findUseCase(),
                useCaseRegisterListener = findUseCase(),
                useCaseGetDvrState = findUseCase(),
                useCaseLockGroups = findUseCase(),
                useCaseUnlockGroups = findUseCase(),
                useCaseDeleteGroups = findUseCase(),
                useCaseUnmountStorage = findUseCase()
            )

            bindViewAndModel(listView, listModel)

            val replayView: ReplayView = ReplayView()
            val replayModel: ReplayModel = ReplayModel(
                onBack = {},
                scope = backgroundScope
            )

            bindViewAndModel(replayView, replayModel)
            

        }catch (e : IllegalStateException){
            currentView.value = { padding -> OnError(padding, e.message ?: "Unknown Error") }
        }
    }

    @Composable
    private fun OnLoading(innerPadding: PaddingValues) {
        var dotCount by remember {
            mutableIntStateOf(1)
        }

        LaunchedEffect(key1 = LocalContext.current) {
            while (true) {
                delay(1000)
                dotCount = (dotCount + 1) % 10 + 1
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Loading\n" + " .".repeat(dotCount),
                textAlign = TextAlign.Center,
                fontSize = 42.sp
            )
        }
    }

    @Composable
    private fun OnError(innerPadding: PaddingValues, message : String){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = message,
                textAlign = TextAlign.Center,
                fontSize = 42.sp)
        }
    }

    private inline fun <reified T : IUseCase> findUseCase(): T {
        return useCaseList.find { it is T } as? T ?: error("UseCase not found: ${T::class.java.name}")
    }

    private fun <S : IUiState, I : IUserIntent, E : IEffect> bindViewAndModel(view : IView<S, I, E>, model : IModel<S, I, E>){
        view.handleEffect(model.effect)
        view.handleUiState(model.state)
        model.handleUserIntent(view.userIntent)
    }
}