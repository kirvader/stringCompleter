import androidx.compose.desktop.Window
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

fun main() = Window(title = "String completer", size = IntSize(500, 700)) {
    val typedTextState = remember { mutableStateOf("") }
    val mapState = remember { mutableStateOf(mutableSetOf<String>()) }
    val indexingStatus = remember { mutableStateOf(IndexingStatus.INDEXING_STOPPED) }


    val autoCompletionItemsFlow = MutableStateFlow(listOf<String>())

    val stringProcessor = Processor(mapState, typedTextState, autoCompletionItemsFlow, indexingStatus)
    stringProcessor.getAllWordsAsync()
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            indexingStatusLabel(indexingStatus = indexingStatus)
            inputField(typedTextState) {
                autoCompletionItemsFlow.value = listOf()
                stringProcessor.updateAllCompletions(0, 200) {
                    // println(it)

                    autoCompletionItemsFlow.value = autoCompletionItemsFlow.value + it
                }
            }

            val autoCompletionItems by autoCompletionItemsFlow.collectAsState()

            val items = (listOf("""For your word "${typedTextState.value}":""") + autoCompletionItems)

            LazyColumn() {
                // val autoCompletionItems by autoCompletionItemsFlow.collectAsState()
               // item { completionChoice("""For your word "${typedTextState.value}":""") }

                items(items = items, itemContent = {word ->
                    completionChoice(word)
                })
            }
        }
    }

}

