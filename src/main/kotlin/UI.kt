import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun inputField(
    typedTextState: MutableState<String>,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = typedTextState.value,
            modifier = Modifier.weight(weight = 1F),
            onValueChange = {
                typedTextState.value = it
            },
            label = { Text(text = "Add a todo") }
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}