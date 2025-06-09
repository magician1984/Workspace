package com.auo.performancetester

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.auo.performancetester.datasource.DataSource
import com.auo.performancetester.datasource.PerformanceMonitor
import com.auo.performancetester.datasource.ResultWriter
import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.FileAllocateMode
import com.auo.performancetester.domain.entity.FileSize
import com.auo.performancetester.domain.entity.ThreadSize
import com.auo.performancetester.domain.usecase.IUseCaseInitialize
import com.auo.performancetester.domain.usecase.IUseCaseListenEvents
import com.auo.performancetester.domain.usecase.IUseCaseStartTest
import com.auo.performancetester.domain.usecase.UseCaseProvider
import com.auo.performancetester.ui.theme.DvrServiceTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            View()
        }
    }

    @Composable
    private fun InitialDataSource(context: Context, func: @Composable (IDataSource) -> Unit) {

        Log.d("Main", "InitialDataSource")

        val logDir = this.filesDir

        Log.d("Main", "Log dir: ${logDir.absolutePath}")

//        val dataSource = DataSource(context, PerformanceMonitor.enable(), ResultWriter.enable(logDir))

        val dataSource =
            DataSource(context, PerformanceMonitor.disable(false), ResultWriter.disable())
        func(dataSource)
    }

    @Composable
    private fun InitialUseCases(
        dataSource: IDataSource,
        func: @Composable (UseCaseProvider) -> Unit
    ) {
        Log.d("Main", "InitialUseCases")
        val useCaseProvider = UseCaseProvider(this, dataSource)
        func(useCaseProvider)
    }

    @Composable
    private fun View() {
        DvrServiceTheme {
            InitialDataSource(this) { dataSource ->
                InitialUseCases(dataSource) { provider ->
//                    val infoModel: InfoModel = InfoModel(
//                        listenEvents = provider.get(IUseCaseListenEvents::class),
//                        initialize = provider.get(IUseCaseInitialize::class)
//                    )
//                    val controlModel: ControlModel = ControlModel(
//                        exitUseCase = provider.get(IUseCaseExit::class),
//                        testUseCase = provider.get(IUseCaseStartTest::class)
//                    )
//
//                    Scaffold(
//                        modifier = Modifier.fillMaxSize(),
//                        topBar = {  }) { innerPadding ->
//                        Column(modifier = Modifier.padding(innerPadding)) {
//                            ControlPage(model = controlModel)
//                            InfoPage( model = infoModel)
//                        }
//                    }

                    val mScope = rememberCoroutineScope()

                    val useCaseInitialize = remember {
                        provider.get(IUseCaseInitialize::class)
                    }

                    val useCaseStartTest = remember {
                        provider.get(IUseCaseStartTest::class)
                    }

                    val useCaseListenEvents = remember {
                        provider.get(IUseCaseListenEvents::class)
                    }

                    val messages = remember {
                        mutableStateListOf<String>()
                    }

                    val focusRequester = remember {
                        FocusRequester()
                    }

                    LaunchedEffect(LocalContext.current) {
                        messages.add("Init datasource")
                        useCaseInitialize()
                        useCaseListenEvents.invoke {
                            messages.add(it.toString())
                        }
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester),
                        topBar = { }) { innerPadding ->
                        Box(modifier = Modifier
                            .padding(innerPadding)
                            .padding(24.dp)) {
                            LazyColumn {
                                items(items = messages) { message ->
                                    Text(text = message)
                                }
                            }

                            Button(
                                modifier = Modifier.align(Alignment.TopEnd),
                                onClick = {

                                    messages.add("Start test")

                                    mScope.launch(Dispatchers.IO) {
                                        useCaseStartTest.invoke(
                                            method = CloneMethod.FileChannel,
                                            count = 50,
                                            forceWrite = true,
                                            threadCount = ThreadSize.One,
                                            fileSize = FileSize.Large,
                                            allocateMode = FileAllocateMode.PreAllocate
                                        )
                                    }

                                }) {
                                Text(text = "Start")
                            }
                        }

                    }
                }
            }
        }
    }

    private fun runProfiler() {


    }
}

