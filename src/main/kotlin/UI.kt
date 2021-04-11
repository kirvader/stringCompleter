import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Отображение текстового поля с возможностью ввода
 */
@Composable
fun inputField(
    typedTextState: MutableState<String>,
    autoCompletionsStatus: MutableState<String>,
    onValueChanged: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = typedTextState.value,
            modifier = Modifier.weight(weight = 1F),
            onValueChange = {
                typedTextState.value = it
                onValueChanged()
            },
            label = { Text(text = autoCompletionsStatus.value) }
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}

/**
 * Отображение статуса загрузки словаря в память
 */
@Composable
fun indexingStatusLabel(
    indexingStatus: MutableState<StringProcessor.Companion.IndexingStatus>
) {
    val label = when (indexingStatus.value) {
        StringProcessor.Companion.IndexingStatus.INDEXING_STOPPED -> "Indexing stopped"
        StringProcessor.Companion.IndexingStatus.INDEXING -> "indexing"
        StringProcessor.Companion.IndexingStatus.MAP_LOADED -> "map successfully loaded"
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Text("Indexing status:")
        Spacer(modifier = Modifier.width(15.dp))
        Text(label, fontWeight = FontWeight.Bold)
    }
}

/**
 * Отображение кликабельного авто-дополнения по его параметрам
 */
@Composable
fun completionChoice(
    word: String,
    onItemClick: ((String) -> Unit)? = null
) {
    Button(
        onClick = {
            onItemClick?.let { lambda -> lambda(word) }
        },
        modifier = Modifier.fillMaxWidth().height(40.dp).border(
            BorderStroke(1.dp, Color.White)
        ),
        colors = ButtonDefaults.textButtonColors(backgroundColor = Color.LightGray)
    ) {

            Text(
                text = word,
                modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth().height(30.dp).absolutePadding(left = 5.dp),
            )

    }
}

/**
 * Отображение заголовка для авто-дополнений
 */
@Composable
fun choicesHeader(wordState: MutableState<String>) {
    Row {
        Text("Auto-completions for the word ", fontStyle = FontStyle.Italic)
        Text(text = wordState.value, fontWeight = FontWeight.Bold)
    }
}

