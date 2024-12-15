package idv.bruce.camera_native

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import idv.bruce.camera_native.presentation.control.ControlModel
import idv.bruce.camera_native.presentation.control.ControlPage
import idv.bruce.camera_native.ui.theme.CameraNativeTheme

class MainActivity : ComponentActivity() {
    private lateinit var controlModel: ControlModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        enableEdgeToEdge()
        setContent {
            View(rendererView = { Box(modifier = it) },
                controlView = { ControlPage(modifier = it, model = controlModel) }
            )
        }
    }

    private fun initialize() {
        controlModel = ControlModel()
    }
}

@Composable
private fun View(
    rendererView: @Composable (Modifier) -> Unit,
    controlView: @Composable (Modifier) -> Unit
) {
    CameraNativeTheme {
        Box {
            rendererView(Modifier.fillMaxSize())
            controlView(Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
@Preview(device = Devices.TABLET)
private fun MainPreview() {
    val model : ControlModel = ControlModel()
    View(rendererView = { Box(modifier = it.background(Color.Blue)) }, controlView = {
        ControlPage(
            modifier = it,
            model = model
        )
    })
}