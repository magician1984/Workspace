package com.auo.dvr_ui.presentation.contents.list.component

import android.graphics.RenderEffect
import android.graphics.Shader
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.contents.CommonComponents
import com.auo.dvr_ui.ui.theme.DvrServiceTheme

object EffectComponent {

    @Composable
    fun DeleteConfirm(onConfirmed: () -> Unit, onDismiss: () -> Unit) {
        Dialog(
            onDismissRequest = onDismiss, properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            val mContext = LocalContext.current
            val mTitle = remember {
                mContext.getString(R.string.select_confirm_title)
            }
            val mMessage = remember {
                mContext.getString(R.string.select_confirm_message)
            }

            val mCancelResource = remember {
                mContext.getString(R.string.select_confirm_dismiss)
            }

            val mConfirmResource = remember {
                mContext.getString(R.string.select_confirm_accept)
            }

            val mCloseResource = remember {
                mutableIntStateOf(R.drawable.ic_close)
            }

            Surface(
                modifier = Modifier.size(width = 1224.dp, height = 500.dp),
                shape = RoundedCornerShape(36.dp),
                tonalElevation = 8.dp,
                color = Color.Transparent
            ) {

                AndroidView(
                    factory = { context ->
                        FrameLayout(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setRenderEffect(
                                RenderEffect.createBlurEffect(
                                    20f, 20f, Shader.TileMode.CLAMP
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF35322B), shape = RoundedCornerShape(36.dp))
                ) {
                    CommonComponents.VectorButton(
                        modifier = Modifier
                            .size(52.dp)
                            .offset((-52).dp, 52.dp),
                        iconRes = mCloseResource.intValue,
                        onClick = onDismiss
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 72.dp, bottom = 102.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            modifier = Modifier,
                            text = mTitle,
                            fontSize = 72.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            modifier = Modifier,
                            text = mMessage,
                            fontSize = 44.sp,
                            color = Color(0xFF9Eb3b6),
                        )
                        Row(
                            modifier = Modifier.padding(top = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(48.dp)
                        ) {
                            CommonComponents.RoundedOutlineButton(
                                modifier = Modifier.size(
                                    width = 438.dp,
                                    height = 80.dp
                                ),
                                label = mCancelResource,
                                borderWidth = 3.dp,
                                textColor = Color.White,
                                textSize = 38.sp,
                                onClick = onDismiss
                            )

                            CommonComponents.AlertButton(
                                modifier = Modifier.size(
                                    width = 438.dp,
                                    height = 80.dp,
                                ),
                                label = mConfirmResource,
                                textSize = 38.sp,
                                onClick = onConfirmed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 1920, heightDp = 1280)
@Composable
private fun ConfirmDeletePreview() {
    DvrServiceTheme {
        EffectComponent.DeleteConfirm(onConfirmed = {}, onDismiss = {})
    }
}