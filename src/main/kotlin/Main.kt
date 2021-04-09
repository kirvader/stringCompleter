import androidx.compose.desktop.Window
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize

fun main() = Window(title = "String completer", size = IntSize(500, 700)) {
    val typedTextState = remember { mutableStateOf("") }
    val loadedMap = remember { mutableStateOf(mutableListOf<String>()) }
    getAllStringsFromMap(loadedMap)


    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            inputField(typedTextState)
            Column(modifier = Modifier.verticalScroll(ScrollState(0))) {
                Text(typedTextState.value)
                // TODO add found strings
            }
        }
    }
}