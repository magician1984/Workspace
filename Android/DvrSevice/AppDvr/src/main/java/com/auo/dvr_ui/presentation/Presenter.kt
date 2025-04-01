package com.auo.dvr_ui.presentation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.sp
import com.auo.dvr_core.CamLocation
import com.auo.dvr_core.RecordType
import com.auo.dvr_ui.entity.IUseCase
import com.auo.dvr_ui.entity.IUseCaseDeleteFile
import com.auo.dvr_ui.entity.IUseCaseGetCacheFile
import com.auo.dvr_ui.entity.IUseCaseGetListFiles
import com.auo.dvr_ui.entity.IUseCaseLockFile
import com.auo.dvr_ui.entity.IUseCaseRegisterListener
import com.auo.dvr_ui.entity.IUseCaseUnlockFile
import com.auo.dvr_ui.entity.RecordFileData
import com.auo.dvr_ui.presentation.contents.CameraLocationView
import com.auo.dvr_ui.presentation.contents.RecordListView
import com.auo.dvr_ui.ui.theme.DvrServiceTheme
import com.auo.dvr_ui.usecase.IPresenter
import kotlinx.coroutines.delay

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
        @Composable
        fun Draw(modifier: Modifier, state: State, onIntent: (IUserIntents) -> Unit)
    }

    private val useCaseList: MutableList<IUseCase> = mutableListOf()

    private val mLocationTabView: IView = CameraLocationView()

    private val mListView: IView = RecordListView()

    private val _state: MutableState<State> =
        mutableStateOf(State(mutableStateListOf(), null, CamLocation.Front, false))

    private var state by _state

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
    }

    override fun onReady() {
        drawContent {

            LaunchedEffect(key1 = LocalContext.current) {
                val list = findUseCase<IUseCaseGetListFiles>()?.invoke() ?: return@LaunchedEffect
                state.fileList.clear()
                state.fileList.addAll(list)

                findUseCase<IUseCaseRegisterListener>()?.invoke { recordList ->
                    Log.d("Trace", "onReady: $recordList")
                    state.fileList.clear()
                    state.fileList.addAll(recordList)
                }?: return@LaunchedEffect
            }

            var handleIntent by remember {
                mutableStateOf<IUserIntents?>(null)
            }

            LaunchedEffect(key1 = handleIntent) {
                val intent = handleIntent ?: return@LaunchedEffect
                processIntent(intent)
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Box(modifier = Modifier.weight(2f)) {

                }
                VerticalDivider()
                Column(modifier = Modifier.weight(3f)) {
                    mLocationTabView.Draw(modifier = Modifier, state = state) { intent ->
                        handleIntent = intent
                    }
                    mListView.Draw(modifier = Modifier, state = state) { intent ->
                        handleIntent = intent
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

    private fun processIntent(intent: IUserIntents) {
        Log.d("Presenter", "handleIntent: $intent")
        when (intent) {
            is IUserIntents.DeleteFile -> findUseCase<IUseCaseDeleteFile>()?.invoke(intent.file)
            is IUserIntents.LockFile -> findUseCase<IUseCaseLockFile>()?.invoke(intent.file)
            is IUserIntents.SelectFile -> state = state.copy(selectedFile = intent.file)
            is IUserIntents.UnlockFile -> findUseCase<IUseCaseUnlockFile>()?.invoke(intent.file)
            is IUserIntents.ViewCameraLocation -> state = state.copy(camLocation = intent.camLocation)
            IUserIntents.ViewNormal -> state = state.copy(isProtected = false)
            IUserIntents.ViewProtected -> state = state.copy(isProtected = true)
            IUserIntents.UnselectFile -> state = state.copy(selectedFile = null)
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

    private inline fun <reified T : IUseCase> findUseCase(): T? {
        return useCaseList.find { it is T } as? T
    }
}