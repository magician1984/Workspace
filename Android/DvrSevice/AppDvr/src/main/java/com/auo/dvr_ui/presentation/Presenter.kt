package com.auo.dvr_ui.presentation

import android.util.Log
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrState
import com.auo.dvr_ui.entity.IUseCase
import com.auo.dvr_ui.entity.IUseCaseDeleteGroups
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetRecordGroups
import com.auo.dvr_ui.entity.IUseCaseLockGroups
import com.auo.dvr_ui.entity.IUseCaseRegisterDvrStateUpdateListener
import com.auo.dvr_ui.entity.IUseCaseRegisterRecordUpdateListener
import com.auo.dvr_ui.entity.IUseCaseUnlockGroups
import com.auo.dvr_ui.framework.ISyncVideoController
import com.auo.dvr_ui.ui.theme.DvrServiceTheme
import com.auo.dvr_ui.usecase.IPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.auo.dvr_ui.presentation.contents.list.Model as ListModel
import com.auo.dvr_ui.presentation.contents.list.View as ListView
import com.auo.dvr_ui.presentation.contents.replay.Model as ReplayModel
import com.auo.dvr_ui.presentation.contents.replay.View as ReplayView

private typealias ViewContent = @Composable (PaddingValues) -> Unit

class Presenter(
    private val renderer: ComponentActivity,
    private val videoController: ISyncVideoController<CamLocation, *>,
    vararg useCases: IUseCase
) : IPresenter {
    sealed class Screen(val route: String) {
        data object List : Screen("list")
        data object Replay : Screen("replay")
    }

    internal interface IUserIntent

    internal interface IUiState

    internal interface IEffect

    internal abstract class IModel<S : IUiState, I : IUserIntent, E : IEffect>(
        protected val scope: CoroutineScope,
        protected val navController: NavHostController
    ) {
        abstract val state: StateFlow<S>
        abstract val effect: StateFlow<E?>

        abstract fun handleUserIntent(intent: I)
    }

    internal abstract class IView<S : IUiState, I : IUserIntent, E : IEffect>(
        protected val state: StateFlow<S>,
        protected val effect: StateFlow<E?>,
        protected val intentHandler: (I) -> Unit
    ) {
        @Composable
        abstract fun Draw(modifier: Modifier)
    }

    private val mUseCaseList: MutableList<IUseCase> = mutableListOf()

    private val mModelList: MutableList<IModel<*, *, *>> = mutableListOf()

    private val mBackgroundScope = CoroutineScope(Dispatchers.IO)

    private val mCurrentView: MutableState<ViewContent> =
        mutableStateOf({ innerPadding -> OnLoading(innerPadding) })

    private val mNavHostController: NavHostController = NavHostController(renderer).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(DialogNavigator())
    }

    init {
        mUseCaseList.addAll(useCases)
        findUseCase<IUseCaseRegisterDvrStateUpdateListener>().invoke(::onDvrStateUpdate)
    }

    override fun render() {
        Log.d("Presenter", "render")
        onDvrStateUpdate();
        renderer.setContent {
            DvrServiceTheme {
                Scaffold { innerPadding ->
                    mCurrentView.value(innerPadding)
                }
            }
        }
    }

    @Composable
    private fun OnReady(innerPadding: PaddingValues) {
        val listView: IView<*, *, *>
        val replayView: IView<*, *, *>

        try {
            val listModel: ListModel = getModel()
            listView = ListView(listModel.state, listModel.effect, listModel::handleUserIntent)

            val replayModel: ReplayModel = getModel()
            replayView =
                ReplayView(replayModel.state, replayModel.effect, replayModel::handleUserIntent)

        } catch (e: IllegalStateException) {
            mCurrentView.value = { padding -> OnError(padding, e.message ?: "Unknown Error") }
            return
        }

        NavHost(navController = mNavHostController, startDestination = Screen.List.route) {
            composable(Screen.List.route) {
                listView.Draw(modifier = Modifier.padding(innerPadding))
            }
            composable(Screen.Replay.route) {
                replayView.Draw(modifier = Modifier.padding(innerPadding))
            }
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
    private fun OnError(innerPadding: PaddingValues, message: String) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = message,
                textAlign = TextAlign.Center,
                fontSize = 42.sp
            )
        }
    }

    private fun onDvrStateUpdate() {
        val state = findUseCase<IUseCaseGetDvrState>().invoke()

        Log.d("Presenter", "onDvrStateUpdate: $state")
        if (state.isAvailable) {
            mCurrentView.value = { innerPadding -> OnReady(innerPadding) }
        } else {
            if (state.errorType == DvrState.ErrorType.None) {
                mCurrentView.value = { innerPadding -> OnLoading(innerPadding) }
            } else {
                mCurrentView.value =
                    { innerPadding -> OnError(innerPadding, state.errorMessage ?: "Unknown") }
            }
        }
    }

    private inline fun <reified T : IModel<*, *, *>> getModel(): T {
        return mModelList.find { it is T } as? T
            ?: run {
                val model = when (T::class) {
                    ListModel::class -> ListModel(
                        scope = mBackgroundScope,
                        navController = mNavHostController,
                        getRecordGroups = { findUseCase<IUseCaseGetRecordGroups>().invoke(set = it) },
                        registerListener = {
                            findUseCase<IUseCaseRegisterRecordUpdateListener>().invoke(
                                it
                            )
                        },
                        lockGroups = { findUseCase<IUseCaseLockGroups>().invoke(it) },
                        unlockGroups = { findUseCase<IUseCaseUnlockGroups>().invoke(it) },
                        deleteGroups = { findUseCase<IUseCaseDeleteGroups>().invoke(it) }
                    ) as T

                    ReplayModel::class -> ReplayModel(
                        scope = mBackgroundScope,
                        navController = mNavHostController,
                        onViewReady = { list ->
                            list.forEach { pair ->
                                videoController.setView(pair.first, pair.second)
                            }
                        },
                        onPlayRequest = {
                            videoController.play()
                        },
                        onPauseRequest = {
                            videoController.pause()
                        },
                        onNextRequest = {},
                        onPrevRequest = {},
                        onPrepareRequest = {
                            mBackgroundScope.launch {
                                it.files.forEach { file ->
                                    videoController.prepare(file.location, file.uri)
                                }
                            }
                        },
                        registerPlayStateUpdate = {
                            videoController.addOnPlayStateUpdateListener(it)
                        },
                        registerPositionUpdate = {
                            videoController.addOnPositionUpdateListener(it)
                        },
                        registerOnVideoReady = {
                            videoController.addOnReadyListener(it)
                        }
                    ) as T

                    else -> error("Model not found: ${T::class.java.name}")
                }
                mModelList.add(model)
                return@run model
            }
    }

    private inline fun <reified T : IUseCase> findUseCase(): T {
        return mUseCaseList.find { it is T } as? T
            ?: error("UseCase not found: ${T::class.java.name}")
    }
}