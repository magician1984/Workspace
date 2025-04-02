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
import com.auo.dvr_ui.entity.IUseCase
import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseUnlockFile
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.presentation.contents.ActionBarView
import com.auo.dvr_ui.presentation.contents.CameraLocationView
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

    internal data class State(
        val fileList: SnapshotStateList<RecordFileData>,
        val selectedFile: RecordFileData?,
        val camLocation: CamLocation,
        val isProtected: Boolean
    )

    internal interface IView {
        val onIntent: (IUserIntents) -> Unit

        @Composable
        fun Draw(modifier: Modifier, state: State)
    }

    private val useCaseList: MutableList<IUseCase> = mutableListOf()

    private val mLocationTabView: IView = CameraLocationView(::onOnIntent)
    private val mListView: IView = RecordListView(::onOnIntent)
    private val mActionBarView : IView = ActionBarView(::onOnIntent)
    private val mReplayView : IView = ReplayView(::onOnIntent)

    private val _state: MutableState<State> =
        mutableStateOf(State(mutableStateListOf(), null, CamLocation.Front, false))

    private var state by _state

    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    override fun summit(
        useCaseGetListFiles: IUseCaseGetListFiles,
        useCaseRegisterListener: IUseCaseRegisterListener,
        useCaseLockFile: IUseCaseLockFile,
        useCaseUnlockFile: IUseCaseUnlockFile,
        useCaseDeleteFile: IUseCaseDeleteFile,
        useCaseGetCacheFile: IUseCaseGetCacheFile
    ) {
        useCaseList.clear()
        useCaseList.addAll(
            listOf(
                useCaseGetListFiles,
                useCaseRegisterListener,
                useCaseLockFile,
                useCaseUnlockFile,
                useCaseDeleteFile,
                useCaseGetCacheFile
            )
        )

        useCaseRegisterListener{
            backgroundScope.launch {
                state = state.copy(fileList = mutableStateListOf<RecordFileData>().apply { addAll(it) })
            }
        }
    }

    override fun onReady() {
        drawContent {

            LaunchedEffect(key1 = LocalContext.current) {
                val list = findUseCase<IUseCaseGetListFiles>()?.invoke() ?: return@LaunchedEffect
                state = state.copy(fileList = mutableStateListOf<RecordFileData>().apply { addAll(list) })
            }

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    mReplayView.Draw(modifier = Modifier.weight(1f), state = state)
                    VerticalDivider()
                    Column(modifier = Modifier.weight(1f)) {
                        mLocationTabView.Draw(modifier = Modifier.height(72.dp), state = state)

                        mListView.Draw(modifier = Modifier.weight(1f), state = state)
                        mActionBarView.Draw(modifier = Modifier.height(72.dp), state = state)
                    }
                }

            }

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

    private fun onOnIntent(intent: IUserIntents) {

        backgroundScope.launch {
            Log.d("Presenter", "handleIntent: $intent")
            when (intent) {
                is IUserIntents.DeleteFile -> findUseCase<IUseCaseDeleteFile>()?.invoke(intent.file)
                is IUserIntents.LockFile -> findUseCase<IUseCaseLockFile>()?.invoke(intent.file)
                is IUserIntents.SelectFile -> state = state.copy(selectedFile = intent.file)
                is IUserIntents.UnlockFile -> findUseCase<IUseCaseUnlockFile>()?.invoke(intent.file)
                is IUserIntents.ViewCameraLocation -> state = state.copy(camLocation = intent.camLocation, selectedFile = null)
                IUserIntents.ViewNormal -> state = state.copy(isProtected = false, selectedFile = null)
                IUserIntents.ViewProtected -> state = state.copy(isProtected = true, selectedFile = null)
                IUserIntents.UnselectFile -> state = state.copy(selectedFile = null)
                is IUserIntents.ReplayFile ->{
                    val cacheFile = findUseCase<IUseCaseGetCacheFile>()?.invoke(state.selectedFile?:return@launch) ?: return@launch
                    val recordFileData = state.selectedFile!!.copy(cacheFile = cacheFile)
                    state = state.copy(selectedFile = recordFileData)
                }
            }
        }
    }

    private inline fun <reified T : IUseCase> findUseCase(): T? {
        return useCaseList.find { it is T } as? T
    }
}