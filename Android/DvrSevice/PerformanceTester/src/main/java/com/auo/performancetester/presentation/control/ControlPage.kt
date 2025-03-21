package com.auo.performancetester.presentation.control

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.auo.performancetester.R
import com.auo.performancetester.presentation.IModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext


@Composable
fun ControlPage(
    modifier: Modifier = Modifier,
    model: IModel<ControlPageIntent, ControlPageState>
) {
    val state by model.state.collectAsState()

    Row(
        modifier = modifier
            .wrapContentHeight()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spinner(
            modifier = Modifier.weight(1.0f, true),
            items = state.methodSpinner.items,
            selectedIndex = state.methodSpinner.selectedIndex
        )
        { index -> model.handleIntent(ControlPageIntent.SelectMethod(index)) }


        Spinner(
            modifier = Modifier.weight(1.0f, true),
            items = state.sizeSpinner.items,
            selectedIndex = state.sizeSpinner.selectedIndex
        ) { index ->
            model.handleIntent(ControlPageIntent.SelectSize(index))
        }


        Spinner(
            modifier = Modifier.weight(1.0f, true),
            items = state.countSpinner.items,
            selectedIndex = state.countSpinner.selectedIndex
        ) { index ->
            model.handleIntent(ControlPageIntent.SelectCount(index))
        }

        ImageButton(
            onClick = { model.handleIntent(ControlPageIntent.ExecuteTest) },
            icon = ImageVector.vectorResource(id = R.drawable.baseline_not_started_24)
        )

        ImageButton(
            onClick = { model.handleIntent(ControlPageIntent.FinishApp) },
            icon = ImageVector.vectorResource(id = R.drawable.baseline_exit_to_app_24)
        )
    }
}

@Composable
fun ImageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enable: Boolean = true,
    icon: ImageVector
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(size = 12.dp)) // To make it a circular button, can be changed to another shape
            .background(
                color = when {
                    !enable -> Color.Gray.copy(alpha = 0.4f)  // Grey out if progress
                    else -> MaterialTheme.colorScheme.surface // Default background
                }
            )
            .clickable(enabled = enable) { onClick.invoke() }
            .padding(16.dp) // Adjust padding as needed
    ) {
        Icon(
            painter = rememberVectorPainter(image = icon),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    items: List<Item>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndexState by remember { mutableStateOf(selectedIndex) }

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = items.getOrElse(selectedIndexState) { Item.CountItem(0) }.name,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item.name) },
                        onClick = {
                            selectedIndexState = index
                            onItemSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(device = Devices.TABLET)
@Composable
fun ImageButtonPreview() {
    ImageButton(
        onClick = {},
        enable = false,
        icon = ImageVector.vectorResource(id = R.drawable.baseline_exit_to_app_24)
    )
}

@Preview(device = Devices.TABLET)
@Composable
fun ControlPagePreview() {
    val model = object : IModel<ControlPageIntent, ControlPageState> {
        override val state: StateFlow<ControlPageState>
            get() = MutableStateFlow(
                ControlPageState(
                    false,
                    SpinnerParam(listOf(), 0),
                    SpinnerParam(listOf(), 0),
                    SpinnerParam(listOf(), 0)
                )
            )
        override val coroutineContext: CoroutineContext
            get() = TODO("Not yet implemented")

        override fun handleIntent(intent: ControlPageIntent) {

        }

    }
    ControlPage(modifier = Modifier.background(Color.White), model = model)
}

@Preview(device = Devices.TABLET)
@Composable
fun SpinnerPreview() {
    Spinner(
        modifier = Modifier,
        items = listOf(),
        selectedIndex = 0,
        onItemSelected = {})
}