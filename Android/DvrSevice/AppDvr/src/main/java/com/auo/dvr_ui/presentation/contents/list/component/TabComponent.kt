package com.auo.dvr_ui.presentation.contents.list.component

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.auo.dvr_ui.presentation.contents.list.UiState

data object TabComponent {
    @Composable
    fun TabLayer(
        modifier: Modifier,
        indicatorColor: Color,
        displayType: UiState.DisplayType,
        onDisplayTypeChanged: (UiState.DisplayType) -> Unit
    ) {

        val tabs = remember {
            buildList { UiState.DisplayType.entries.forEach { type -> add(type.name) } }
        }

        val selectedIndex by remember(displayType) {
            mutableIntStateOf(displayType.code)
        }

        TabRow(modifier = modifier, selectedTabIndex = selectedIndex) {
            tabs.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                Tab(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected)
                            onDisplayTypeChanged(UiState.DisplayType.entries[index])
                    },
                    text = { Text(text = label) },
                    selectedContentColor = indicatorColor,
                    unselectedContentColor = Color.White
                )
            }
        }
    }
}