import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp


@Composable
fun inputField(
    typedTextState: MutableState<String>,
    label: MutableState<String>,
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
            label = { Text(text = label.value) }
        )

        Spacer(modifier = Modifier.width(8.dp))
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
