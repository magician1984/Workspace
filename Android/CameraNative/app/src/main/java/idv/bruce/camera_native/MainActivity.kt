package idv.bruce.camera_native

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import idv.bruce.camera_native.presentation.control.ControlModel
import idv.bruce.camera_native.presentation.control.ControlPage
import idv.bruce.camera_native.core.theme.CameraNativeTheme
import idv.bruce.camera_native.datasource.AVMSource
import idv.bruce.camera_native.domain.usecase.impl.UseCaseStartPreview
import idv.bruce.camera_native.domain.usecase.impl.UseCaseStopPreview
import idv.bruce.camera_native.domain.usecase.impl.UseCaseSwitchMode
import idv.bruce.camera_native.domain.usecase.impl.UseCaseSyncStatus
import idv.bruce.camera_native.presentation.preview.PreviewModel
import idv.bruce.camera_native.presentation.preview.PreviewPage

class MainActivity : ComponentActivity() {
    private lateinit var avmSource: AVMSource

    private lateinit var controlModel: ControlModel
    private lateinit var previewModel: PreviewModel

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        enableEdgeToEdge()
        setContent {

            val permissionState =
                rememberPermissionState(permission = android.Manifest.permission.CAMERA)

            LaunchedEffect(key1 = LocalContext.current) {
                if(!permissionState.status.isGranted)
                    permissionState.launchPermissionRequest()
            }

            if (permissionState.status.isGranted) {
                View(
                    rendererView = { PreviewPage(modifier = it, model = previewModel) },
                    controlView = { ControlPage(modifier = it, model = controlModel) }
                )
            }else{
                Text(text = "No Permission", fontSize = 72.sp)
            }
        }

    }

    private fun initialize() {
        avmSource = AVMSource()

        controlModel = ControlModel(
            switchMode = UseCaseSwitchMode(avmSource),
            syncStatus = UseCaseSyncStatus(avmSource)
        )

        previewModel = PreviewModel(
            startPreview = UseCaseStartPreview(avmSource),
            stopPreview = UseCaseStopPreview(avmSource)
        )

//        startService(Intent(this, AVMService::class.java))
    }
}

@Composable
private fun View(
    rendererView: @Composable (Modifier) -> Unit,
    controlView: @Composable (Modifier) -> Unit
) {
    CameraNativeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                rendererView(Modifier.fillMaxSize())
                controlView(Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

@Composable
@Preview(device = Devices.TABLET)
private fun MainPreview() {
    val model: ControlModel = ControlModel()
    View(rendererView = { Box(modifier = it.background(Color.Blue)) }, controlView = {
        ControlPage(
            modifier = it,
            model = model
        )
    })
}