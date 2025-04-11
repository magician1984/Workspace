package com.auo.dvr_ui.presentation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.entity.IUseCase
import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseGetDvrState
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterDvrStateListener
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseUnlockFile
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.presentation.contents.ActionBarView
import com.auo.dvr_ui.presentation.contents.RecordListView
import com.auo.dvr_ui.presentation.contents.ReplayView
import com.auo.dvr_ui.ui.theme.DvrServiceTheme
import com.auo.dvr_ui.usecase.IPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Presenter(
    private val renderer: ComponentActivity
) : IPresenter {

    internal sealed class Effect {
        data class OnError(val message: String) : Effect()
        data class OnRemoveProtectedFile(val file: RecordFileData) : Effect()
    }

    internal data class State(
        val fileList: SnapshotStateList<RecordFileData>,
        val selectedFile: RecordFileData?,
        val camLocation: CamLocation,
        val isProtected: Boolean,
        val effect: Effect?
    )

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
        mutableStateOf(State(mutableStateListOf(), null, CamLocation.Front, false, null))

    private var state by _state

    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    override fun summit(
        useCaseGetListFiles: IUseCaseGetListFiles,
        useCaseRegisterListener: IUseCaseRegisterListener,
        useCaseLockFile: IUseCaseLockFile,
        useCaseUnlockFile: IUseCaseUnlockFile,
        useCaseDeleteFile: IUseCaseDeleteFile,
        useCaseGetCacheFile: IUseCaseGetCacheFile,
        useCaseGetDvrState: IUseCaseGetDvrState,
        useCaseRegisterDvrStateListener: IUseCaseRegisterDvrStateListener
    ) {
        useCaseList.clear()
        useCaseList.addAll(
            listOf(
                useCaseGetListFiles,
                useCaseRegisterListener,
                useCaseLockFile,
                useCaseUnlockFile,
                useCaseDeleteFile,
                useCaseGetCacheFile,
                useCaseGetDvrState,
                useCaseRegisterDvrStateListener
            )
        )

        useCaseRegisterListener {
            backgroundScope.launch {
                state =
                    state.copy(fileList = mutableStateListOf<RecordFileData>().apply {
                        addAll(it.filter {
                            if (state.isProtected)
                                it.type == RecordType.Protected
                            else
                                it.type != RecordType.Protected
                        })
                    })
            }
        }

        useCaseRegisterDvrStateListener {
            backgroundScope.launch {
                val errorEffect =
                    if (!it.isAvailable) Effect.OnError(it.errorMessage ?: "") else null
                state = state.copy(effect = errorEffect)
            }
        }
    }

    override fun onReady() {
        drawContent {

            LaunchedEffect(key1 = LocalContext.current) {
                val list = findUseCase<IUseCaseGetListFiles>()?.invoke()?.filter { item ->
                    if (state.isProtected) item.type == RecordType.Protected else item.type != RecordType.Protected
                } ?: return@LaunchedEffect
                val dvrState = findUseCase<IUseCaseGetDvrState>()?.invoke() ?: return@LaunchedEffect
                val errorEffect = if (!dvrState.isAvailable) Effect.OnError(
                    dvrState.errorMessage ?: ""
                ) else state.effect

                state = state.copy(
                    fileList = mutableStateListOf<RecordFileData>().apply { addAll(list) },
                    effect = errorEffect
                )
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
                        state = state.copy(selectedFile = null)
                }

                is IUserIntents.LockFile -> findUseCase<IUseCaseLockFile>()?.invoke(intent.file)
                is IUserIntents.SelectFile -> state = state.copy(selectedFile = intent.file)
                is IUserIntents.UnlockFile -> findUseCase<IUseCaseUnlockFile>()?.invoke(intent.file)
                is IUserIntents.ViewCameraLocation -> state =
                    state.copy(camLocation = intent.camLocation, selectedFile = null)

                IUserIntents.ViewNormal -> {
                    val fileList = findUseCase<IUseCaseGetListFiles>()?.invoke()
                        ?.filter { it.type != RecordType.Protected } ?: emptyList()

                    state =
                        state.copy(fileList = mutableStateListOf<RecordFileData>().apply {
                            addAll(
                                fileList
                            )
                        }, isProtected = false, selectedFile = null)
                }

                IUserIntents.ViewProtected -> {
                    val fileList = findUseCase<IUseCaseGetListFiles>()?.invoke()
                        ?.filter { it.type == RecordType.Protected } ?: emptyList()
                    state =
                        state.copy(fileList = mutableStateListOf<RecordFileData>().apply {
                            addAll(
                                fileList
                            )
                        }, isProtected = true, selectedFile = null)
                }

                IUserIntents.UnselectFile -> state = state.copy(selectedFile = null)
                is IUserIntents.ReplayFile -> {
                    val cacheFile = findUseCase<IUseCaseGetCacheFile>()?.invoke(
                        state.selectedFile ?: return@launch
                    ) ?: return@launch
                    val recordFileData = state.selectedFile!!.copy(cacheFile = cacheFile)
                    state = state.copy(selectedFile = recordFileData)
                }

                is IUserIntents.ConfirmDeleteFile -> {
                    state = state.copy(effect = Effect.OnRemoveProtectedFile(intent.file))
                }
            }
        }
    }

    @Composable
    private fun ProcessEffect(effect: Effect?) {
        val mEffect = effect ?: return

        when (mEffect) {
            is Effect.OnError -> {
                Dialog(onDismissRequest = {}) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .fillMaxHeight(0.3f)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = mEffect.message ?: "", modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(), textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is Effect.OnRemoveProtectedFile -> {
                AlertDialog(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(16.dp),
                    onDismissRequest = { /*TODO*/ },
                    title = { Text(text = "Confirm Delete") },
                    text = { Text(text = "Are you sure you want to delete this file?") },
                    dismissButton = {
                        TextButton(onClick = { state = state.copy(effect = null) }) {
                            Text(text = "Dismiss")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            state = state.copy(effect = null)
                            onIntent(IUserIntents.DeleteFile(mEffect.file))
                        }) {
                            Text(text = "Confirm")
                        }
                    })
            }
        }
    }

    private inline fun <reified T : IUseCase> findUseCase(): T? {
        return useCaseList.find { it is T } as? T
    }
}