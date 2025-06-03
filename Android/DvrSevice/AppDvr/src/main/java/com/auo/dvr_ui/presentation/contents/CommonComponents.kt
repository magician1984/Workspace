package com.auo.dvr_ui.presentation.contents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
}

@Preview
@Composable
private fun CircleButtonPreview() {
    DvrServiceTheme {
        CommonComponents.CircleButton(
            modifier = Modifier.size(92.dp),
            iconRes = R.drawable.icon_close,
            backgroundColor = Color.Gray,
            tintColor = Color.White,
            onClick = {})
    }
}