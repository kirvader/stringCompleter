import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

enum class IndexingStatus {
    INDEXING_STOPPED, INDEXING, MAP_LOADED
}

class Processor(
    private val mapState: MutableState<MutableSet<String>>,
    private val typedWordState: MutableState<String>,
    private var choiceCompletions: MutableStateFlow<List<String>>,
    private val indexingStatus: MutableState<IndexingStatus>
) {


    fun getAllWordsAsync() {
        indexingStatus.value = IndexingStatus.INDEXING
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
            val reader = BufferedReader(InputStreamReader(url.openStream()))
            val buffer = mutableListOf<String>()
            var line: String?
            while (reader.readLine().also { line = it } != null && line != null) {
                buffer.add(line!!)
                if (buffer.size == bufferSize) {
                    mapState.value.addAll(buffer)
                    // println(buffer)
                    buffer.clear()
                }
            }
            indexingStatus.value = IndexingStatus.MAP_LOADED
        }
    }

    fun updateAllCompletions(
        startValue: Int,
        offset: Int,
        onAddElement: (String) -> Unit
    ) {

        GlobalScope.launch(Dispatchers.Main) {
            allCompletionsFlow(startValue, offset).map{value ->
                onAddElement(value)

                value
            }.collect()
        }

    }

    fun allCompletionsFlow(startValue: Int, offset: Int) : Flow<String> = flow {
        // TODO получать данные между startValue и startValue + offset

        if (typedWordState.value.isNotEmpty()) {
            var count = 0
            mapState.value.forEach {
                if (isSubstring(typedWordState.value, it)) {
                    count++
                    if (count > startValue && count < startValue + offset)
                        emit(it)
                    if (count > startValue + offset)
                        return@forEach
                }
            }
        }
    }

    private fun isSubstring(sub: String, text: String): Boolean {
        return text.contains(sub)
    }

    companion object {
        private const val bufferSize: Int = 1000

    }
}