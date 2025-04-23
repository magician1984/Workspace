package com.auo.dvr_ui.presentation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.entity.IUseCase
import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseGetConfigure
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterDvrStateListener
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseSetConfigure
import com.auo.dvr_ui.entity.IUseCaseUnlockFile
import com.auo.dvr_ui.entity.IUseCaseUnmountStorage
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.presentation.contents.ActionBarView
import com.auo.dvr_ui.presentation.contents.RecordListView
import com.auo.dvr_ui.presentation.contents.ReplayView
import com.auo.dvr_ui.presentation.effect.EffectViewProvider
import com.auo.dvr_ui.ui.theme.DvrServiceTheme
import com.auo.dvr_ui.usecase.IPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class Presenter(
    private val renderer: ComponentActivity
) : IPresenter {

    internal sealed class Effect {
        data class OnError(val message: String) : Effect()
        data class OnRemoveProtectedFile(val file: RecordFileData) : Effect()
        data class OnSetting(val configure: DvrConfigure) : Effect()
        data object OnUnmountStorage : Effect()
    }

    internal data class State(
        val fileList: SnapshotStateList<RecordFileData>,
        val selectedFile: RecordFileData?,
        val camLocation: CamLocation,
        val isProtected: Boolean,
        val playingFile: File?,
        val effect: Effect?
    ) {
        companion object {
            fun parseList(
                list: List<RecordFileData>,
                isProtected: Boolean
            ): SnapshotStateList<RecordFileData> {
                return mutableStateListOf<RecordFileData>().apply {
                    addAll(list.filter {
                        if (isProtected)
                            it.type == RecordType.Protected
                        else
                            it.type != RecordType.Protected
                    })
                }
            }
        }
    }

    internal interface IView {
        val onIntent: (IUserIntents) -> Unit

        @Composable
        fun Draw(modifier: Modifier, state: State)
    }

    private val useCaseList: MutableList<IUseCase> = mutableListOf()

    private val mListView: IView = RecordListView(::onIntent)
    private val mActionBarView: IView = ActionBarView(::onIntent)
    private val mReplayView: IView = ReplayView(::onIntent)

    private val _state: MutableState<State> =
        mutableStateOf(State(mutableStateListOf(), null, CamLocation.Front, false, null, null))

    private var state by _state

    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    override fun summit(vararg useCases: IUseCase) {
        useCaseList.clear()
        useCaseList.addAll(useCases)

        findUseCase<IUseCaseRegisterListener>()?.invoke {
            backgroundScope.launch {
                Log.d("Presenter", "onUpdate: ${it.size}")
                updateState(fileList = State.parseList(it, state.isProtected))
            }
        }

        findUseCase<IUseCaseRegisterDvrStateListener>()?.invoke {
            backgroundScope.launch {
                val errorEffect =
                    if (!it.isAvailable) Effect.OnError(it.errorMessage ?: "") else null

                val fileList: List<RecordFileData> = if (it.isAvailable)
                    findUseCase<IUseCaseGetListFiles>()?.invoke() ?: emptyList()
                else
                    emptyList()

                updateState(
                    fileList = State.parseList(fileList, state.isProtected),
                    effect = errorEffect
                )
            }
        }
    }

    override fun onReady() {
        drawContent {

            LaunchedEffect(key1 = LocalContext.current) {
                findUseCase<IUseCaseGetDvrState>()?.invoke()?.let { dvrState ->
                    val effect = if (!dvrState.isAvailable) Effect.OnError(
                        dvrState.errorMessage ?: ""
                    ) else null
                    val fileList =
                        if (dvrState.isAvailable) findUseCase<IUseCaseGetListFiles>()?.invoke()
                            ?: emptyList() else emptyList()
                    Log.d("Presenter", "Effect : $effect, List size : ${fileList.size}")
                    updateState(State.parseList(fileList, state.isProtected), effect = effect)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    mReplayView.Draw(modifier = Modifier.weight(1f), state = state)
                    VerticalDivider()
                    Column(modifier = Modifier.weight(1f)) {
                        mListView.Draw(modifier = Modifier.weight(1f), state = state)
                        mActionBarView.Draw(modifier = Modifier.height(72.dp), state = state)
                    }
                }
            }

            ProcessEffect(effect = state.effect)
        }
    }

    override fun onLoading() {
        drawContent {
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
                    .padding(it)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Loading\n" + " .".repeat(dotCount),
                    textAlign = TextAlign.Center,
                    fontSize = 42.sp
                )
            }
        }
    }

    private fun drawContent(content: @Composable (PaddingValues) -> Unit) {
        renderer.setContent {
            DvrServiceTheme {
                Scaffold { innerPadding ->
                    content(innerPadding)
                }
            }
        }
    }

    private fun onIntent(intent: IUserIntents) {

        backgroundScope.launch {
            Log.d("Presenter", "handleIntent: $intent")
            when (intent) {
                is IUserIntents.DeleteFile -> {
                    findUseCase<IUseCaseDeleteFile>()?.invoke(intent.file)
                    if (intent.file.id == state.selectedFile?.id)
                        updateState(selectedFile = null)
                }

                is IUserIntents.LockFile -> findUseCase<IUseCaseLockFile>()?.invoke(intent.file)
                is IUserIntents.SelectFile -> updateState(selectedFile = intent.file, playingFile = null)
                is IUserIntents.UnlockFile -> findUseCase<IUseCaseUnlockFile>()?.invoke(intent.file)
                is IUserIntents.ViewCameraLocation -> updateState(
                    camLocation = intent.camLocation,
                    selectedFile = null
                )

                IUserIntents.ViewNormal -> {
                    val fileList = findUseCase<IUseCaseGetListFiles>()?.invoke()
                        ?.filter { it.type != RecordType.Protected } ?: emptyList()

                    updateState(
                        fileList = State.parseList(fileList, false),
                        isProtected = false,
                        selectedFile = null
                    )
                }

                IUserIntents.ViewProtected -> {
                    val fileList = findUseCase<IUseCaseGetListFiles>()?.invoke()
                        ?.filter { it.type == RecordType.Protected } ?: emptyList()
                    updateState(
                        fileList = State.parseList(fileList, true),
                        isProtected = true,
                        selectedFile = null
                    )
                }

                IUserIntents.UnselectFile -> state = state.copy(selectedFile = null, playingFile = null)
                is IUserIntents.RequestPlayFile -> {
                    val cacheFile = findUseCase<IUseCaseGetCacheFile>()?.invoke(
                        state.selectedFile ?: return@launch
                    ) ?: return@launch

                    updateState(playingFile = cacheFile)
                }
                is IUserIntents.ReleasePlayFile -> {
                    updateState(playingFile = null)
                }

                is IUserIntents.ConfirmDeleteFile -> {
                    updateState(effect = Effect.OnRemoveProtectedFile(intent.file))
                }

                IUserIntents.ConfirmUnmountStorage -> {
                    updateState(effect = Effect.OnUnmountStorage)
                }

                IUserIntents.OpenSettings -> {
                    val configure = findUseCase<IUseCaseGetConfigure>()?.invoke() ?: return@launch
                    updateState(effect = Effect.OnSetting(configure))
                }

                IUserIntents.UnmountStorage -> {
                    findUseCase<IUseCaseUnmountStorage>()?.invoke()
                }

                is IUserIntents.UpdateConfigure -> {
                    findUseCase<IUseCaseSetConfigure>()?.invoke(intent.configure)
                }
            }
        }
    }

    @Composable
    private fun ProcessEffect(effect: Effect?) {
        val mEffect = effect ?: return

        when (mEffect) {
            is Effect.OnError -> {
                EffectViewProvider.ErrorDialog(message = mEffect.message)
            }

            is Effect.OnRemoveProtectedFile -> {
                EffectViewProvider.DeleteConfirmDialog(onConfirm = {
                    updateState(effect = null)
                    onIntent(IUserIntents.DeleteFile(mEffect.file))
                }, onDismiss = {
                    updateState(effect = null)
                })
            }

            is Effect.OnSetting -> {
                EffectViewProvider.SettingDialog(mEffect.configure, onConfirm = {
                    onIntent(IUserIntents.UpdateConfigure(it))
                    updateState(effect = null)
                }, onDismiss = {
                    updateState(effect = null)
                })
            }

            Effect.OnUnmountStorage -> {
                EffectViewProvider.UnmountConfirmDialog(onConfirm = {
                    onIntent(IUserIntents.UnmountStorage)
                    updateState(effect = null)
                }, onDismiss = {
                    updateState(effect = null)
                })
            }
        }
    }

    private inline fun <reified T : IUseCase> findUseCase(): T? {
        return useCaseList.find { it is T } as? T
    }

    private fun updateState(
        fileList: SnapshotStateList<RecordFileData> = state.fileList,
        selectedFile: RecordFileData? = state.selectedFile,
        camLocation: CamLocation = state.camLocation,
        isProtected: Boolean = state.isProtected,
        playingFile: File? = state.playingFile,
        effect: Effect? = state.effect
    ) {
        Log.d(
            "Presenter",
            "updateState: $fileList, $selectedFile, $camLocation, $isProtected, $playingFile, $effect"
        )
        state = state.copy(
            fileList = fileList,
            selectedFile = selectedFile,
            camLocation = camLocation,
            isProtected = isProtected,
            playingFile = playingFile,
            effect = effect
        )
    }
}