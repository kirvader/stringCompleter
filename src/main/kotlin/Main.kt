import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

fun main() = Window(title = "String completer", size = IntSize(500, 700)) {
    val stringProcessor = Processor()

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            indexingStatusLabel(indexingStatus = stringProcessor.indexingStatus)
            inputField(stringProcessor.typedWordState, stringProcessor.autoCompletionStatus) {
                stringProcessor.updateAllCompletions()
            }

            val autoCompletionItems by stringProcessor.collectStateFlowAsState()

            choicesHeader(stringProcessor.typedWordState)

            LazyColumn {

                items(items = autoCompletionItems, itemContent = { word ->
                    completionChoice(word) {
                        stringProcessor.typedWordState.value = it
                        stringProcessor.updateAllCompletions()
                    }
                })
            }
        }
    }
}

