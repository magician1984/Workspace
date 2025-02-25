package idv.bruce.camera_native.presentation.preview

import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG : String = "PreviewPage"

@Composable
fun PreviewPage(modifier: Modifier = Modifier, model: PreviewModel) {
    AndroidView(factory = {
        SurfaceView(it).apply {
            this.holder.addCallback(object : SurfaceHolder.Callback2{
                override fun surfaceCreated(holder: SurfaceHolder) {
                    Log.d(TAG, "surfaceCreated")
                    model.handleIntent(PreviewPageIntent.StartPreview(holder.surface))
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    Log.d(TAG, "surfaceChanged: $format, $width, $height")
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    Log.d(TAG, "surfaceDestroyed")
                    model.handleIntent(PreviewPageIntent.StopPreview)
                }

                override fun surfaceRedrawNeeded(holder: SurfaceHolder) {

                }

            })
        }
    }, modifier = modifier)
}