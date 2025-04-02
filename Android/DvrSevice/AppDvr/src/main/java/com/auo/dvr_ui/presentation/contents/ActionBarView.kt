package com.auo.dvr_ui.presentation.contents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.IUserIntents
import com.auo.dvr_ui.presentation.Presenter

internal class ActionBarView(override val onIntent: (IUserIntents) -> Unit) : Presenter.IView {
    @Composable
    override fun Draw(modifier: Modifier, state: Presenter.State) {
        val normalIconRes = remember {
            R.drawable.baseline_directions_car_filled_24
        }

        val protectedIconRes = remember {
            R.drawable.baseline_car_crash_24
        }

        val settingIconRes = remember {
            R.drawable.baseline_settings_24
        }

        Row(modifier = modifier.padding(8.dp)) {
            TypeButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                res = painterResource(id = normalIconRes),
                isSelected = !state.isProtected
            ) {
                onIntent(IUserIntents.ViewNormal)
            }
            Spacer(modifier = modifier.width(16.dp))
            TypeButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                res = painterResource(id = protectedIconRes),
                isSelected = state.isProtected
            ) {
                onIntent(IUserIntents.ViewProtected)
            }
            Spacer(modifier = modifier.weight(1f))
            Icon(
                painter = painterResource(id = settingIconRes),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            )
        }
    }

    @Composable
    private fun TypeButton(
        modifier: Modifier,
        res: Painter,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {

        val scale by animateFloatAsState(if (isSelected) 1.01f else 1f, label = "item selected")

        val tintColor =
            if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current

        Icon(painter = res, contentDescription = "", tint = tintColor, modifier = modifier
            .scale(scale)
            .clickable(enabled = !isSelected) { onClick() })
    }
}