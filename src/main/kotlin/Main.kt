import androidx.compose.desktop.Window
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

fun main() = Window(title = "String completer", size = IntSize(500, 700)) {
    val typedTextState = remember { mutableStateOf("") }
    val mapState = remember { mutableStateOf(mutableSetOf<String>()) }
    val currentCompletions = remember { mutableStateOf(mutableListOf<String>()) }
    val currentActionStatus = remember { mutableStateOf("Indexing") }


    val stringProcessor = Processor(mapState, typedTextState, currentCompletions)
    currentActionStatus.value = "Ready"


    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            inputField(typedTextState, currentActionStatus, stringProcessor::updateAllCompletions)

            Column(modifier = Modifier.verticalScroll(state = ScrollState(0))) {
                completionChoice("""For your word "${typedTextState.value}":""")
                for (word in stringProcessor.getAllCompletions()) {
                    completionChoice(word)
                }
            }
        }
    }
}