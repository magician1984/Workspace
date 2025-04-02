package com.auo.dvr_ui.presentation.contents

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.auo.dvr_core.CamLocation
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.IUserIntents
import com.auo.dvr_ui.presentation.Presenter

internal class CameraLocationView(override val onIntent: (IUserIntents) -> Unit) : Presenter.IView {
    @Composable
    override fun Draw(
        modifier: Modifier,
        state: Presenter.State
    ) {
        val tabItems = remember {
            listOf(
                Pair(CamLocation.Front, R.drawable.baseline_filter_1_24),
                Pair(CamLocation.Rear, R.drawable.baseline_filter_2_24),
                Pair(CamLocation.Left, R.drawable.baseline_filter_3_24),
                Pair(CamLocation.Right, R.drawable.baseline_filter_4_24),
            )
        }

        TabRow(modifier = modifier, selectedTabIndex = tabItems.indexOfFirst { it.first == state.camLocation }){
            tabItems.forEachIndexed { index, pair ->
                val isSelected = index == tabItems.indexOfFirst { it.first == state.camLocation }
                Tab(
                    modifier = Modifier.fillMaxHeight(),
                    selected = isSelected,
                    onClick = {
                        if(!isSelected)
                            onIntent(IUserIntents.ViewCameraLocation(pair.first))
                    }) {
                    Icon(painter = painterResource(id = pair.second), contentDescription = null, modifier = Modifier.size(64.dp).padding(bottom = 4.dp, top = 4.dp))
                }
            }
        }
    }
}