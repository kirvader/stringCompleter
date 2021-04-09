import androidx.compose.runtime.MutableState
import java.io.IOException

import java.net.MalformedURLException

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.concurrent.thread

private const val bufferSize: Int = 1000

fun getAllStringsFromMap(currentMap: MutableState<MutableList<String>>) {
    val url = URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
    val reader = BufferedReader(InputStreamReader(url.openStream()))
    var quantity = 0
    val buffer = mutableListOf<String>()
    var line: String?

    while (reader.readLine().also { line = it } != null && line != null) {
        buffer.add(line!!)
        if (buffer.size == bufferSize) {
            currentMap.value.addAll(buffer)
            println(buffer)
            buffer.clear()
        }
        quantity++
    }
    println(quantity)
    reader.close()
}