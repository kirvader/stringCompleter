import androidx.compose.runtime.MutableState
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class Processor(private val currentMapState: MutableState<MutableSet<String>>, private val typedWordState: MutableState<String>, private val choiceCompletions: MutableState<MutableList<String>>) {

    init {
        val url = URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
        val reader = BufferedReader(InputStreamReader(url.openStream()))
        val buffer = mutableListOf<String>()
        var line: String?
        while (reader.readLine().also { line = it } != null && line != null) {
            buffer.add(line!!)
            if (buffer.size == bufferSize) {
                currentMapState.value.addAll(buffer)
                // println(buffer)
                buffer.clear()
            }
        }
        reader.close()
    }

    fun updateAllCompletions() {
        println("updating")
        choiceCompletions.value.clear()
        choiceCompletions.value.addAll(currentMapState.value.filter { it.contains(typedWordState.value) })
        println(choiceCompletions.value)
    }

    fun getAllCompletions() = choiceCompletions.value.take(10)

    companion object {
        private const val bufferSize: Int = 1000
    }
}