package com.auo.dvr_ui.presentation.contents.replay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.auo.dvr_ui.R
import com.auo.dvr_ui.presentation.Presenter
import com.auo.dvr_ui.presentation.contents.replay.component.DisplayComponent
import kotlinx.coroutines.flow.StateFlow

internal class View(
    state: StateFlow<UiState>,
    effect: StateFlow<Effect?>,
    intentHandler: (UserIntent) -> Unit
) : Presenter.IView<UiState, UserIntent, Effect>(state, effect, intentHandler) {
    companion object{
        private val MEDIA_BTN_SPACE = 88.dp
        private val PROGRESS_HEIGHT = 8.dp
        private val CONTROL_BAR_HEIGHT = 181.dp
        private val CONTROL_BAR_INNER_PADDING = PaddingValues(
            start = 46.dp,
            top = 30.dp,
            bottom = 26.dp
        )
    }

    @Composable
    override fun Draw(modifier: Modifier) {
        val mState by state.collectAsState()
        val mEffect = effect.collectAsState()

        val mContext = LocalContext.current

        val mProgressBgColor = remember{
            Color(mContext.getColor(R.color.color_progress_background))
        }
        val mProgressTrackColor = remember{
            Color(mContext.getColor(R.color.color_progress_track))
        }

        Column(modifier = modifier) {
            DisplayComponent.DisplayLayer(modifier = Modifier.weight(1f), mState, onReady = {
                intentHandler(UserIntent.SurfaceReady(it))
            })
            DisplayComponent.ControlLayer(modifier = Modifier
                .fillMaxWidth()
                .height(CONTROL_BAR_HEIGHT),
                isPlaying = mState.isPlaying,
                time = 0L,
                config = DisplayComponent.ControlLayerConfig(
                    progressHigh = PROGRESS_HEIGHT,
                    progressBackgroundColor = mProgressBgColor,
                    progressTrackColor = mProgressTrackColor,
                    mediaBtnSpace = MEDIA_BTN_SPACE,
                    btnLayerPadding = CONTROL_BAR_INNER_PADDING
                ),
                onPlayStateSwitch = {intentHandler(UserIntent.PlayStateSwitch)},
                onPrevious = {intentHandler(UserIntent.Previous)},
                onNext = {intentHandler(UserIntent.Next)},
                onBack = {intentHandler(UserIntent.Back)}
            )
        }
    }
}