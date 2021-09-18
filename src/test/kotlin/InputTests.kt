import input.Option
import input.parseAllKeys
import kotlin.test.*

internal class InputTests {

    @Test
    fun parseAllKeysTestNoArgs() {
        val test: List<String> = listOf()
        val correct = mapOf(
            Option.HELP to "false",
            Option.ENABLE_CONTEXT to "false"
        )
        assertEquals(correct, parseAllKeys(test))
    }

    @Test
    fun parseAllKeysTestOnlyKeysWithoutArgs1() {
        val test: List<String> = listOf("-h", "-o")
        val correct = mapOf(
            Option.HELP to "true",
            Option.ENABLE_CONTEXT to "true"
        )
        assertEquals(correct, parseAllKeys(test))
    }

    @Test
    fun parseAllKeysTestOnlyKeysWithoutArgs2() {
        val test: List<String> = listOf("--help", "-h")
        val correct = mapOf(
            Option.HELP to "true",
            Option.ENABLE_CONTEXT to "false"
        )
        assertEquals(correct, parseAllKeys(test))
    }

    @Test
    fun parseAllKeysTestOnlyArgsWithKeys1() {
        val test: List<String> = listOf("-f", "/\\", "-w", "80", "-s", "short", "-c", "series", "-d", "series")
        val correct = mapOf(
            Option.HELP to "false",
            Option.ENABLE_CONTEXT to "false",
            Option.FILE to "/\\",
            Option.WIDTH to "80",
            Option.SIGN_MODE to "short",
            Option.COMMON_MODE to "series",
            Option.DIFF_MODE to "series"
        )
        assertEquals(correct, parseAllKeys(test))
    }

    @Test
    fun parseAllKeysTestOnlyArgsWithKeys2() {
        val test: List<String> = listOf("--sign", "121", "--border", "100")
        val correct = mapOf(
            Option.HELP to "false",
            Option.ENABLE_CONTEXT to "false",
            Option.SIGN_MODE to "121",
            Option.CONTEXT_BORDER to "100"
        )
        assertEquals(correct, parseAllKeys(test))
    }

    @Test
    fun parseAllKeysTest1() {
        val test: List<String> = listOf("--context", "-o", "keke", "-b", "100", "-o")
        val correct = mapOf(
            Option.HELP to "false",
            Option.ENABLE_CONTEXT to "true",
            Option.CONTEXT_BORDER to "100"
        )
        assertEquals(correct, parseAllKeys(test))
    }

    @Test
    fun parseAllKeysTest2() {
        val test: List<String> = listOf("--context", "-o", "keke", "-b", "kekk", "-o")
        val correct = mapOf(
            Option.HELP to "false",
            Option.ENABLE_CONTEXT to "true",
            Option.CONTEXT_BORDER to "kekk"
        )
        assertEquals(correct, parseAllKeys(test))
    }
}