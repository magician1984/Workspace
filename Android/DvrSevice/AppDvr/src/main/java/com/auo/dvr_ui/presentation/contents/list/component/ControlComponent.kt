package com.auo.dvr_ui.presentation.contents.list.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.contents.CommonComponents

data object ControlComponent {
    private val OUTLINE_BOARD_WIDTH = 2.dp

    data class ControlLayerConfig(
        val controlLayerWidth: Dp,
        val controlLayerHeight: Dp,
        val innerPadding: PaddingValues,
        val btnSpace: Dp,
        val textSize: TextUnit,
        val selectModeToggleConfig: SelectModeToggleConfig
    )

    data class SelectModeToggleConfig(
        val width : Dp,
        val height : Dp,
        val textSize : TextUnit,
        val borderColor : Color,
        val borderWidth : Dp,
        val backgroundColor : Color,
        val textColor : Color
    )

    @Composable
    fun ControlLayer(
        modifier: Modifier,
        selectMode: Boolean,
        selectedCount: Int,
        config: ControlLayerConfig,
        onSelectModeChanged: (Boolean) -> Unit,
        onDeleteRequest: () -> Unit,
        onLockRequest: () -> Unit,
        onUnlockRequest: () -> Unit
    ) {
        Box(modifier = modifier) {
            if (selectMode) {
                SelectModeControlBar(
                    modifier = Modifier
                        .size(config.controlLayerWidth, config.controlLayerHeight)
                        .align(Alignment.Center),
                    selectedCount = selectedCount,
                    onSelectModeChanged = onSelectModeChanged,
                    paddingValues = config.innerPadding,
                    btnSpace = config.btnSpace,
                    textSize = config.textSize,
                    onLockRequest = onLockRequest,
                    onUnlockRequest = onUnlockRequest,
                    onDeleteRequest = onDeleteRequest
                )
            } else {
                EnableSelectModeButton(
                    modifier = Modifier.align(Alignment.Center),
                    config = config.selectModeToggleConfig,
                    onClick = { onSelectModeChanged(true) })
            }
        }
    }

    @Composable
    fun SelectAllButtonLayer(
        modifier: Modifier,
        boardColor: Color,
        onClick: () -> Unit
    ) {
        val mContext: Context = LocalContext.current

        val mLabel = remember {
            mContext.getText(R.string.select_all).toString()
        }

        CommonComponents.RoundedButton(
            modifier = modifier,
            label = mLabel,
            borderColor = boardColor,
            borderWidth = OUTLINE_BOARD_WIDTH,
            backgroundColor = Color.Transparent,
            textColor = Color.White,
            onClick = onClick
        )
    }

    @Composable
    private fun EnableSelectModeButton(modifier: Modifier, config : SelectModeToggleConfig, onClick: () -> Unit) {
        val mContext: Context = LocalContext.current

        val mEnableSelectModeLabel = remember {
            mContext.getText(R.string.select_recording).toString()
        }

        CommonComponents.RoundedButton(
            modifier = modifier.size(config.width, config.height),
            label = mEnableSelectModeLabel,
            borderColor = config.borderColor,
            borderWidth = config.borderWidth,
            backgroundColor = config.backgroundColor,
            textColor = config.textColor,
            textSize = config.textSize,
            onClick = onClick
        )
    }

    @Composable
    private fun SelectModeControlBar(
        modifier: Modifier,
        selectedCount: Int,
        paddingValues: PaddingValues,
        btnSpace: Dp,
        textSize: TextUnit,
        onLockRequest: () -> Unit,
        onUnlockRequest: () -> Unit,
        onDeleteRequest: () -> Unit,
        onSelectModeChanged: (Boolean) -> Unit
    ) {
        Box(
            modifier = modifier
                .background(
                    color = Color.DarkGray,
                    shape = CircleShape
                )
                .padding(paddingValues)
        ) {
            CommonComponents.VectorButton(
                modifier = Modifier.align(Alignment.CenterStart),
                iconRes = R.drawable.ic_close,
                onClick = { onSelectModeChanged(false) }
            )

            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(btnSpace)
            ) {
                CommonComponents.VectorButton(
                    modifier = Modifier,
                    iconRes = R.drawable.btn_unlock,
                    touchedRes = R.drawable.btn_unlock_pressed,
                    onClick = onUnlockRequest
                )

                CommonComponents.VectorButton(
                    modifier = Modifier,
                    iconRes = R.drawable.btn_lock,
                    touchedRes = R.drawable.btn_lock_pressed,
                    onClick = onLockRequest
                )

                CommonComponents.VectorButton(
                    modifier = Modifier,
                    iconRes = R.drawable.btn_delete,
                    touchedRes = R.drawable.btn_delete_pressed,
                    onClick = onDeleteRequest
                )
            }

            SelectedCountLabel(
                modifier = Modifier.align(Alignment.CenterEnd),
                selectedCount = selectedCount,
                textSize = textSize
            )
        }
    }


    @Composable
    private fun SelectedCountLabel(modifier: Modifier, selectedCount: Int, textSize: TextUnit) {
        Text(modifier = modifier, text = "$selectedCount selected", fontSize = textSize)
    }
}