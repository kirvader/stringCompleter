import Processor.Companion.AutoCompletionsStatus.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL



class Processor() {

    /**
     * Изменяющееся состояние словаря, с которым работаем
     */
    private val mapState: MutableState<MutableList<String>> = mutableStateOf(mutableListOf())

    /**
     * Статус, индексирования словаря mapState
     */
    val indexingStatus: MutableState<IndexingStatus> = mutableStateOf(IndexingStatus.INDEXING_STOPPED)

    /**
     * Состояние строки, введенной пользователем в данный момент
     */
    val typedWordState: MutableState<String> = mutableStateOf("")

    /**
     * Статус прогресса обработки словаря на авто-дополнения введенной пользователем строки
     */
    val autoCompletionStatus = mutableStateOf(getCompletionsStatus(READY_TO_START))

    /**
     * Состояние потока, в который складываются найденные авто-дополнения
     */
    private val autoCompletionItemsFlow = MutableStateFlow(listOf<String>())

    init {
        getAllWordsAsync()
    }

    /**
     * getAllWordsAsync - функция позволяющая в отдельном потоке получить все слова находящиеся по указанному в MAP_URL URL
     *
     * Она работает следующим образом:
     * Есть буфер маленького размера который нужно заполнить
     * Далее с блокировкой на словарь в памяти добавляем все элементы из буфера в словарь и чистим буфер
     * Так идем до конца
     *
     */
    private fun getAllWordsAsync() {
        indexingStatus.value = IndexingStatus.INDEXING
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL(MAP_URL)
            val reader = BufferedReader(InputStreamReader(url.openStream()))
            val buffer = mutableListOf<String>()
            var line: String?
            while (reader.readLine().also { line = it } != null && line != null) {
                buffer.add(line!!)
                if (buffer.size == MAP_LOADING_BUFFER_SIZE) {
                    synchronized(mapState) {
                        mapState.value.addAll(buffer)
                    }
                    buffer.clear()
                }
            }
            indexingStatus.value = IndexingStatus.MAP_LOADED
        }
    }

    /**
     * currentJob отвечает за последнюю корутину, которая была запущена
     *
     * Нужна для того, чтобы при вводе пользователем следующей буквы,
     * можно было перестать обрабатывать предыдущую строчку и перейти к следующей
     *
     */
    private var currentJob: Job? = null


    /**
     * updateAllCompletions() обновляет все авто-дополнения для отображения их на экран, а именно:
     *
     * Если все еще идет обработка предыдущего, введенного пользователем слова, то нужно прервать эту обработку
     *
     * Если пользователь удалил все до пустой строки, то можно ничего и не выводить, чтобы не тратить ресурсы
     *
     * Иначе создаем новую отдельную корутину, запускаем в ней flow (nextCompletionsFlow) и из него получаем все новые авто-дополнения
     * Если после получения очередной пачки авто-дополнений пользователь не изменил строку,
     * то можно попробовать получить еще одну пачку авто-дополнений.
     *
     */
    fun updateAllCompletions() {
        autoCompletionItemsFlow.value = listOf()
        startValue = 0
        if (currentJob != null) {
            currentJob!!.cancel()
            setAutoCompletingStatus(FINISHED)
        }
        if (typedWordState.value.isNotEmpty()) {
            currentJob = GlobalScope.launch(Dispatchers.Main) {
                setAutoCompletingStatus(IN_PROCESS)
                while (!currentJob!!.isCancelled && startValue < mapState.value.size) {
                    nextCompletionsFlow().map { completion ->
                        autoCompletionItemsFlow.value = autoCompletionItemsFlow.value + completion
                    }.collect()
                    delay(1)
                }
                setAutoCompletingStatus(FINISHED)
            }
        } else {
            setAutoCompletingStatus(READY_TO_START)
        }

    }

    /**
     * Запускает flow, в котором ищет очередную небольшую пачку авто-дополнений, подходящих к введенному слову.
     *
     * Здесь точно стоит отметить мое предположение о том, что для достаточно небольшого размера искомой
     * пачки авто-дополнений ответ ищется достаточно быстро. Это происходит из-за очень большого размера
     * словаря различных, осмысленных слов, а значит более-менее равномерного распределения
     * слов в словаре по содержанию в них строчки какой-то фиксированной длины.
     *
     */
    private fun nextCompletionsFlow(): Flow<String> = flow {
        val completionsBuffer = mutableListOf<String>()
        synchronized(mapState) {
            while (startValue < mapState.value.size) {
                val word = mapState.value[startValue]
                if (isSubsequence(typedWordState.value, word)) {
                    if (completionsBuffer.size < COMPLETION_BUFFER_SIZE) {
                        completionsBuffer.add(word)
                    }
                    if (completionsBuffer.size >= COMPLETION_BUFFER_SIZE)
                        break
                }
                startValue++
            }
        }
        completionsBuffer.forEach {
            emit(it)
        }

    }

    /**
     * Получает все полученные авто-дополнения в виде состояния, для отображения на экран
     */
    @Composable
    fun collectStateFlowAsState() = autoCompletionItemsFlow.collectAsState()

    /**
     * Проверка на принадлежность строки sub строке text в качестве подпоследовательности
     *
     * Будем это делать следующим образом:
     *
     * Будем искать именно первое вхождение в качестве подпоследовательности
     *
     * Для этого будем всегда искать самое первое вхождение буквы после
     * самого раннего вхождения префикса идущего до нее проверяемого subsequence
     *
     * Соответственно если мы для всех элементов предполагаемой подпоследовательности нашли их ранние отображения, то
     * это действительно подпоследовательность.
     * Если же мы не смогли найти, то она не является подпоследовательностью. Докажем это, пойдя от противного:
     *
     * пусть это подпоследовательность а мы сказали что нет, тогда выберем в text последовательные индексы {i_0, i_1, ...}
     * Тогда subsequence[0] = text[i_0], subsequence[1] = text[i_1], ... Тогда для каждого из элементов в subsequence
     * можно выбрать индекс меньше соответствующего, то есть если обозначить первое вхождение элемента k после
     * первого вхождения всех предыдущих за inning[k], то inning[k] <= i_k. Значит inning[i_n] < i_n, где n = subsequence.length
     */
    private fun isSubsequence(subsequence: String, text: String): Boolean {
        var indexSub = 0 // последний индекс в subsequence, для которого нет отображения на обработанном префиксе text
        if (subsequence.length > text.length) return false

        for (char in text) {
            if (char == subsequence[indexSub]) indexSub++

            if (indexSub == subsequence.length) return true // если последний индекс для которого
        // нет отображения большего его размера, значит мы это действительно подпоследовательность
        }
        return false
    }

    /**
     * Сеттер состояния авто-дополнений в качестве строки
     */
    private fun setAutoCompletingStatus(status: AutoCompletionsStatus) {
        autoCompletionStatus.value = getCompletionsStatus(status)
    }

    /**
     * Обработка статуса обработки авто-дополнений
     */
    private fun getCompletionsStatus(status: AutoCompletionsStatus) = when (status) {
        READY_TO_START -> "Ready to find"
        IN_PROCESS -> "In progress"
        FINISHED -> "Finished"
    }


    companion object {
        // URL на котором находится словарь
        private const val MAP_URL = "https://raw.githubusercontent.com/dwyl/english-words/master/words.txt"

        // размер буфера, который используется при загрузке словаря
        private const val MAP_LOADING_BUFFER_SIZE: Int = 1000

        // размер буфера, который используется при обработке авто-дополнений
        private const val COMPLETION_BUFFER_SIZE: Int = 100

        // индекс элемента в словаре на котором остановилась обработка авто-дополнений в прошлый раз
        private var startValue: Int = 0

        enum class IndexingStatus {
            INDEXING_STOPPED,
            INDEXING,
            MAP_LOADED
        }

        enum class AutoCompletionsStatus {
            READY_TO_START,
            IN_PROCESS,
            FINISHED
        }
    }
}