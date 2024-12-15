package idv.bruce.camera_native.presentation.control

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import idv.bruce.camera_native.R
import idv.bruce.camera_native.domain.entity.AVMStatus
import idv.bruce.camera_native.domain.entity.PreviewMode
import idv.bruce.camera_native.ui.theme.CameraNativeTheme

@Composable
fun ControlPage(
    modifier: Modifier = Modifier,
    model : ControlModel
) {
    val state = model.state.collectAsState()

    val buttons = remember {
        listOf(
            Pair(PreviewMode.GRID, R.drawable.baseline_grid_view_24),
            Pair(PreviewMode.AVM, R.drawable.baseline_surround_sound_24),
            Pair(PreviewMode.SINGLE(0), R.drawable.baseline_filter_1_24),
            Pair(PreviewMode.SINGLE(1), R.drawable.baseline_filter_2_24),
            Pair(PreviewMode.SINGLE(2), R.drawable.baseline_filter_3_24),
            Pair(PreviewMode.SINGLE(3), R.drawable.baseline_filter_4_24)
        )
    }

    Row(modifier = modifier) {
        buttons.forEach {
            StateButton(
                modifier = Modifier,
                res = ImageVector.vectorResource(id = it.second),
                isActive = (state.value.avmStatus is AVMStatus.STREAMING) && ((state.value.avmStatus as AVMStatus.STREAMING).previewMode == it.first),
                onClick = {
                    model.handleIntent(ControlPageIntent.SwitchMode(it.first))
                }
            )
        }
    }
}


@Composable
private fun StateButton(
    modifier: Modifier = Modifier,
    res: ImageVector,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
    isActive: Boolean = false,
    isProgress: Boolean = false,
    onClick: (() -> Unit) = {}
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(size = 12.dp)) // To make it a circular button, can be changed to another shape
            .background(
                color = when {
                    isProgress -> Color.Gray.copy(alpha = 0.4f)  // Grey out if progress
                    isActive -> highlightColor // Active state highlights the button
                    else -> MaterialTheme.colorScheme.surface // Default background
                }
            )
            .clickable(enabled = !isProgress) { onClick.invoke() }
            .padding(16.dp) // Adjust padding as needed
    ) {
        if (isProgress) {
            // Show a progress indicator (like a CircularProgressIndicator) when the button is in progress state
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            // Display the VectorDrawable icon
            Icon(
                painter = rememberVectorPainter(image = res),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
@Preview
private fun StateButtonPreview() {
    CameraNativeTheme {
        StateButton(
            modifier = Modifier,
            res = ImageVector.vectorResource(id = R.drawable.baseline_filter_1_24),
            isProgress = false,
            isActive = true
        )
    }
}

@Composable
@Preview(device = Devices.PIXEL_TABLET)
private fun ControlPagePreview() {
    CameraNativeTheme {
        ControlPage(model = ControlModel())
    }
}