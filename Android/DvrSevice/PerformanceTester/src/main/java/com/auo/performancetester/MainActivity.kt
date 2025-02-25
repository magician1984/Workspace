package com.auo.performancetester

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.auo.performancetester.datasource.DataSource
import com.auo.performancetester.domain.datasource.IDataSource
import com.auo.performancetester.domain.usecase.IUseCaseExit
import com.auo.performancetester.domain.usecase.IUseCaseInitialize
import com.auo.performancetester.domain.usecase.IUseCaseListenEvents
import com.auo.performancetester.domain.usecase.IUseCaseStartTest
import com.auo.performancetester.domain.usecase.UseCaseProvider
import com.auo.performancetester.domain.usecase.impl.UseCaseListenEvents
import com.auo.performancetester.presentation.control.ControlModel
import com.auo.performancetester.presentation.control.ControlPage
import com.auo.performancetester.presentation.info.InfoModel
import com.auo.performancetester.presentation.info.InfoPage
import com.auo.performancetester.ui.theme.DvrServiceTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

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

        val dataSource = DataSource(context)

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
                    val infoModel: InfoModel = InfoModel(
                        listenEvents = provider.get(IUseCaseListenEvents::class),
                        initialize = provider.get(IUseCaseInitialize::class)
                    )
                    val controlModel: ControlModel = ControlModel(
                        exitUseCase = provider.get(IUseCaseExit::class),
                        testUseCase = provider.get(IUseCaseStartTest::class)
                    )

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {  }) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            ControlPage(model = controlModel)
                            InfoPage( model = infoModel)
                        }

                    }
                }
            }
        }
    }

    @Preview(device = Devices.TABLET)
    @Composable
    fun Preview() {
        View()
    }
}

