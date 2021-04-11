import kotlin.reflect.KFunction

import org.junit.jupiter.api.Test
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals


class StringProcessorTest {
    private val TestingObject = StringProcessor()


    private fun callPrivate(objectInstance: Any, methodName: String, vararg args: Any?): Any? {
        val privateMethod: KFunction<*>? =
            objectInstance::class.functions.find { t -> return@find t.name == methodName }

        val argList = args.toMutableList()
        (argList as ArrayList).add(0, objectInstance)
        val argArr = argList.toArray()

        privateMethod?.apply {
            isAccessible = true
            return call(*argArr)
        }
            ?: throw NoSuchMethodException("Method $methodName does not exist in ${objectInstance::class.qualifiedName}")
        return null
    }


    fun testIsSubsequence(subSequence: String, text: String): Boolean {
        return callPrivate(TestingObject, "isSubsequence", subSequence, text) as Boolean
    }

    @Test
    fun `test isSubsequence empty sub` () {
        val string = "".toLowerCase()
        val text = "abracadabra".toLowerCase()
        val expected = false
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence blank sub` () {
        val string = "          ".toLowerCase()
        val text = "abracadabra".toLowerCase()
        val expected = false
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence sub larger than text` () {
        val string = "abracadabraheh".toLowerCase()
        val text = "abracadabra".toLowerCase()
        val expected = false
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence empty text` () {
        val string = "abracadabra".toLowerCase()
        val text = "".toLowerCase()
        val expected = false
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence not subsequence but almost` () {
        val string = "tralalald".toLowerCase()
        val text = "tralalala".toLowerCase()
        val expected = false
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence not subsequence` () {
        val string = "tralalala".toLowerCase()
        val text = "abracadabra".toLowerCase()
        val expected = false
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence true substring` () {
        val string = "tralalala".toLowerCase()
        val text = "tralalala".toLowerCase()
        val expected = true
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence true subsequence` () {
        val string = "tra".toLowerCase()
        val text = "tralalala".toLowerCase()
        val expected = true
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence true in the end` () {
        val string = "urt".toLowerCase()
        val text = "agent Kurt".toLowerCase()
        val expected = true
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }

    @Test
    fun `test isSubsequence true not in row` () {
        val string = "llll".toLowerCase()
        val text = "olololololo".toLowerCase()
        val expected = true
        assertEquals(expected, testIsSubsequence(string, text), "${this::`test isSubsequence empty sub`.toString()} failed")
    }
}