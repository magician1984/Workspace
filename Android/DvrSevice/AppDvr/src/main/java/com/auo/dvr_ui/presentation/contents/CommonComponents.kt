package com.auo.dvr_ui.presentation.contents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auo.dvr_ui.R
import com.auo.dvr_ui.ui.theme.DvrServiceTheme

object CommonComponents {
    @Composable
    fun RoundedOutlineButton(
        modifier: Modifier,
        label: String,
        textSize: TextUnit = 32.sp,
        borderWidth: Dp,
        textColor: Color,
        onClick: () -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val startColor by remember(isPressed) {
            mutableStateOf(
                if (isPressed)
                    Color(0xFFFFFFFF)
                else
                    Color(0xFFFFC000)
            )
        }

        val endColor by remember(isPressed) {
            mutableStateOf(
                if (isPressed)
                    Color(0xFFFFCB55)
                else
                    Color(0xFFFF9C00)
            )
        }

        val mLabel by remember(label) {
            mutableStateOf(label)
        }

        Box(
            modifier = modifier
                .border(
                    border = BorderStroke(
                        borderWidth,
                        Brush.verticalGradient(listOf(startColor, endColor))
                    ),
                    RoundedCornerShape(50)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = mLabel,
                fontSize = textSize,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun AlertButton(
        modifier: Modifier,
        label: String,
        textSize: TextUnit = 32.sp,
        onClick: () -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val startColor by remember(isPressed) {
            mutableStateOf(
                if (isPressed)
                    Color(0xFFA51E1E)
                else
                    Color(0xFFC82626)
            )
        }

        val endColor by remember(isPressed) {
            mutableStateOf(
                if (isPressed)
                    Color(0xFF4D0907)
                else
                    Color(0xFF7E0D0A)
            )
        }

        val mLabel by remember(label) {
            mutableStateOf(label)
        }

        Box(
            modifier = modifier
                .background(
                    brush = Brush.verticalGradient(listOf(startColor, endColor)),
                    shape = RoundedCornerShape(50)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = mLabel,
                fontSize = textSize,
                color = Color.White,
                textAlign = TextAlign.Center
            )
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

        val changeAlpha = remember {
            iconRes == touchedRes
        }

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
                .alpha(if(changeAlpha && isPressed) 0.6f else 1f)
        )
    }
}

@Preview
@Composable
private fun AlertButtonPreview() {
    DvrServiceTheme {
        CommonComponents.AlertButton(
            modifier = Modifier.size(width = 408.dp, height = 100.dp),
            label = "Pressed",
            textSize = 54.sp,
            onClick = {})
    }
}

@Preview
@Composable
private fun RoundedOutlineButtonPreview() {
    DvrServiceTheme {
        CommonComponents.RoundedOutlineButton(
            modifier = Modifier.size(width = 408.dp, height = 100.dp),
            label = "Pressed",
            borderWidth = 4.dp,
            textColor = Color.White,
            textSize = 54.sp,
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
            iconRes = R.drawable.btn_back_pressed,
            touchedRes = R.drawable.btn_back_pressed,
            onClick = {}
        )
    }
}