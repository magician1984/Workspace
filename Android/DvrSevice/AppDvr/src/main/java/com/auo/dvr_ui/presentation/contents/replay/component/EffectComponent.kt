package com.auo.dvr_ui.presentation.contents.replay.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object EffectComponent {
    @Composable
    fun OnLoading(){
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {},
            title = null,
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 16.dp),
                        strokeWidth = 2.dp
                    )
                    Text(text = "OnLoading")
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}