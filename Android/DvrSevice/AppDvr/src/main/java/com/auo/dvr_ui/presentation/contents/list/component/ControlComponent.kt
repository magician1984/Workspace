package com.auo.dvr_ui.presentation.contents.list.component

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.contents.CommonComponents

data object ControlComponent {
    private val OUTLINE_BOARD_WIDTH = 2.dp
    private val CONTROL_BAR_PADDING = 8.dp

    @Composable
    fun ControlLayer(
        modifier: Modifier,
        highlightColor: Color,
        selectMode: Boolean,
        selectedCount: Int,
        onSelectModeChanged: (Boolean) -> Unit,
        onDeleteRequest: () -> Unit,
        onLockRequest: () -> Unit,
        onUnlockRequest: () -> Unit
    ) {
        Box(modifier = modifier.padding(16.dp)) {
            if (selectMode) {
                SelectModeControlBar(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.8f)
                        .align(Alignment.Center),
                    selectedCount = selectedCount,
                    onSelectModeChanged = onSelectModeChanged,
                    onLockRequest = onLockRequest,
                    onUnlockRequest = onUnlockRequest,
                    onDeleteRequest = onDeleteRequest
                )
            } else {
                EnableSelectModeButton(
                    modifier = Modifier.align(Alignment.Center),
                    boardColor = highlightColor,
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

        HighlightButton(
            modifier = modifier,
            label = mLabel,
            boardColor = boardColor,
            onClick = onClick
        )
    }

    @Composable
    private fun EnableSelectModeButton(modifier: Modifier, boardColor: Color, onClick: () -> Unit) {
        val mContext: Context = LocalContext.current

        val mEnableSelectModeLabel = remember {
            mContext.getText(R.string.select_recording).toString()
        }

        HighlightButton(
            modifier = modifier,
            label = mEnableSelectModeLabel,
            boardColor = boardColor,
            onClick = onClick
        )
    }

    @Composable
    private fun SelectModeControlBar(
        modifier: Modifier,
        selectedCount: Int,
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
                ).padding(top = CONTROL_BAR_PADDING, bottom = CONTROL_BAR_PADDING, end = CONTROL_BAR_PADDING)
        ) {
            CommonComponents.CircleButton(
                modifier = Modifier.align(Alignment.CenterStart),
                iconRes = R.drawable.icon_close,
                backgroundColor = Color.Transparent,
                tintColor = Color.White,
            ) { onSelectModeChanged(false) }

            Row(modifier = Modifier.align(Alignment.Center), horizontalArrangement = Arrangement.spacedBy(CONTROL_BAR_PADDING)) {
                CommonComponents.CircleButton(
                    modifier = Modifier,
                    iconRes = R.drawable.icon_unlock,
                    backgroundColor = Color.Gray,
                    tintColor = Color.White,
                    onClick = onUnlockRequest
                )
                CommonComponents.CircleButton(
                    modifier = Modifier,
                    iconRes = R.drawable.icon_lock,
                    backgroundColor = Color.Gray,
                    tintColor = Color.White,
                    onClick = onLockRequest
                )
                CommonComponents.CircleButton(
                    modifier = Modifier,
                    iconRes = R.drawable.icon_delete,
                    backgroundColor = Color.Gray,
                    tintColor = Color.White,
                    onClick = onDeleteRequest
                )
            }

            SelectedCountLabel(
                modifier = Modifier.align(Alignment.CenterEnd),
                selectedCount = selectedCount
            )
        }
    }

    @Composable
    private fun HighlightButton(
        modifier: Modifier,
        label: String,
        boardColor: Color,
        onClick: () -> Unit
    ) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick,
            shape = CircleShape,
            border = BorderStroke(OUTLINE_BOARD_WIDTH, boardColor),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            val mLabel by remember(label) {
                mutableStateOf(label)
            }

            Text(text = mLabel, style = MaterialTheme.typography.labelLarge)
        }
    }

    @Composable
    private fun SelectedCountLabel(modifier: Modifier, selectedCount: Int) {
        Text(modifier = modifier, text = "$selectedCount selected")
    }
}