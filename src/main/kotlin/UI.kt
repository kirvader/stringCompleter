import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import java.time.format.TextStyle


@Composable
fun inputField(
    typedTextState: MutableState<String>,
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
            label = { Text(text = "Type here") }
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun indexingStatusLabel(
    indexingStatus: MutableState<IndexingStatus>
) {
    val label = when(indexingStatus.value) {
        IndexingStatus.INDEXING_STOPPED -> "Indexing stopped"
        IndexingStatus.INDEXING -> "indexing"
        IndexingStatus.MAP_LOADED -> "map successfully loaded"
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Text("Indexing status:")
        Spacer(modifier = Modifier.width(15.dp))
        Text(label, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun completionChoice(
    word: String
) {
    Row(modifier = Modifier.height(30.dp).fillMaxWidth().border(
        BorderStroke(1.dp, Color.LightGray)
    )) {
        Text(word, modifier = Modifier.align(Alignment.CenterVertically).absolutePadding(left = 5.dp))
    }
}

@Composable
fun showCompletions(
    completionsState: MutableState<MutableList<String>>
) {
    println(completionsState.value)
    Column (modifier = Modifier.verticalScroll(ScrollState(0))){
        for (word in completionsState.value) {
            completionChoice(word)
        }
    }
}
