package com.auo.dvr_ui.presentation.effect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.auo.dvr_core.DvrConfigure
import com.auo.dvr_core.RecordDuration
import com.auo.dvr_core.RecordResolution

internal object EffectViewProvider {

    private val durationMap = mutableMapOf<RecordDuration, String>().apply {
        put(RecordDuration.FiveMin, "5 min")
        put(RecordDuration.ThreeMin, "3 min")
        put(RecordDuration.OneMin, "1 min")
    }

    @Composable
    fun ErrorDialog(message: String) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.3f)
                    .padding(16.dp)
            ) {
                Text(
                    text = message, modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(), textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    fun DeleteConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            onDismissRequest = { /*TODO*/ },
            title = { Text(text = "Confirm Delete") },
            text = { Text(text = "Are you sure you want to delete this file?") },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "Confirm")
                }
            })
    }

    @Composable
    fun UnmountConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            onDismissRequest = { /*TODO*/ },
            title = { Text(text = "Confirm Unmount Storage") },
            text = { Text(text = "Are you sure you want to unmount storage?") },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "Confirm")
                }
            })
    }

    @Composable
    fun SettingDialog(
        configure: DvrConfigure,
        onConfirm: (DvrConfigure) -> Unit,
        onDismiss: () -> Unit
    ) {
        var mConfigure by remember { mutableStateOf(configure) }



        Dialog(onDismissRequest = { /*TODO*/ }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Duration: ")
                        RecordDuration.entries.forEach {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                                mConfigure = mConfigure.copy(duration = it)
                            }) {
                                RadioButton(
                                    modifier = Modifier.padding(8.dp),
                                    selected = mConfigure.duration == it,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors()
                                )
                                Text(text = durationMap[it] ?: "")
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Resolution: ")
                        RecordResolution.entries.forEach {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                                mConfigure = mConfigure.copy(resolution = it)
                            }) {
                                RadioButton(
                                    modifier = Modifier.padding(8.dp),
                                    selected = mConfigure.resolution == it,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors()
                                )
                                Text(text = it.name)
                            }
                        }
                    }
                    HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = { onConfirm(mConfigure) }) {
                            Text(text = "Apply")
                        }
                        TextButton(modifier = Modifier.weight(1f), onClick = onDismiss) {
                            Text(text = "Cancel")
                        }
                    }
                }

            }
        }
    }
}