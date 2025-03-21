package com.auo.performancetester.presentation.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.auo.performancetester.domain.entity.CloneMethod
import com.auo.performancetester.domain.entity.IData
import com.auo.performancetester.presentation.IModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

@Composable
fun InfoPage(modifier: Modifier = Modifier, model: IModel<InfoIntent, InfoState>){
    val state = model.state.collectAsState()

    LaunchedEffect(key1 = LocalContext.current) {
        model.handleIntent(InfoIntent.PagePrepare)
    }

    Row(modifier = modifier) {
        MessagePage(modifier = Modifier.weight(1f, true), messages = state.value.messages)
        ResultsPage(modifier = Modifier.weight(1f, true),results = state.value.results)
    }
}

@Composable
fun MessagePage(modifier: Modifier = Modifier, messages: List<IData.EventMessage>){
    LazyColumn(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { message ->
            MessageItem(message = message)
        }
    }
}

@Composable
fun MessageItem(message: IData.EventMessage){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(text = message.toString(), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ResultsPage(modifier: Modifier = Modifier, results: List<IData.TestResult>) {
    LazyColumn(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(results) { result ->
            ResultItem(result = result)
        }
    }
}

@Composable
fun ResultItem(result: IData.TestResult) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(text = result.toString(), fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun ResultsPagePreview() {
    ResultsPage(
        results = listOf(
            IData.TestResult(100000L, 10, CloneMethod.FileChannel, 10000000L),
            IData.TestResult(100000L, 10, CloneMethod.FileChannel, 10000000L),
            IData.TestResult(100000L, 10, CloneMethod.FileChannel, 10000000L),
        )
    )
}

@Preview(device = Devices.TABLET)
@Composable
fun InfoPagePreview(){
    val model : IModel<InfoIntent, InfoState> = object : IModel<InfoIntent, InfoState>{
        override val state: StateFlow<InfoState>
            get() = MutableStateFlow(InfoState(
                messages = listOf(
                    IData.EventMessage("Message 1"),
                    IData.EventMessage("Message 2"),
                    IData.EventMessage("Message 3"),
                ),
                results = listOf(
                    IData.TestResult(100000L, 10, CloneMethod.FileChannel, 10000000L),
                )
            ))
        override val coroutineContext: CoroutineContext
            get() = TODO("Not yet implemented")

        override fun handleIntent(intent: InfoIntent) {
            TODO("Not yet implemented")
        }
    }

    InfoPage(model = model)
}
