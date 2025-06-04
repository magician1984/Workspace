package com.auo.dvr_ui.presentation.contents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auo.dvr_ui.R
import com.auo.dvr_ui.ui.theme.DvrServiceTheme

object CommonComponents {
    @Composable
    fun CircleButton(
        modifier: Modifier,
        iconRes: Int,
        backgroundColor: Color,
        tintColor: Color,
        onClick: () -> Unit
    ) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .clickable(onClick = onClick)
                .background(color = backgroundColor, shape = CircleShape)
                .padding(8.dp)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(tintColor)
            )
        }
    }

    @Composable
    fun RoundedButton(
        modifier: Modifier,
        label: String,
        textSize: TextUnit = 32.sp,
        borderColor: Color,
        borderWidth: Dp,
        backgroundColor: Color,
        textColor: Color,
        onClick: () -> Unit
    ) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick,
            shape = CircleShape,
            border = BorderStroke(borderWidth, borderColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backgroundColor,
                contentColor = textColor
            )
        ) {
            val mLabel by remember(label) {
                mutableStateOf(label)
            }

            Text(text = mLabel, fontSize = textSize)
        }
    }

    @Composable
    fun VectorButton(
        modifier: Modifier,
        iconRes: Int,
        touchedRes: Int = iconRes,
        onClick: () -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val imageVector = ImageVector.vectorResource(id = if (isPressed) touchedRes else iconRes)

        Image(
            imageVector = imageVector,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // 可自定指示效果
                    onClick = onClick
                )
        )
    }
}

@Preview
@Composable
private fun CircleButtonPreview() {
    DvrServiceTheme {
        CommonComponents.CircleButton(
            modifier = Modifier.size(92.dp),
            iconRes = R.drawable.ic_close,
            backgroundColor = Color.Gray,
            tintColor = Color.White,
            onClick = {})
    }
}

@Preview
@Composable
private fun RoundedButtonPreview() {
    DvrServiceTheme {
        CommonComponents.RoundedButton(
            modifier = Modifier.wrapContentWidth(),
            label = "Hello World",
            borderColor = Color.Gray,
            borderWidth = 4.dp,
            backgroundColor = Color.White,
            textColor = Color.Black,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun VectorButtonPreview() {
    DvrServiceTheme {
        CommonComponents.VectorButton(
            modifier = Modifier.size(64.dp),
            iconRes = R.drawable.btn_back,
            touchedRes = R.drawable.btn_back_pressed,
            onClick = {}
        )
    }
}