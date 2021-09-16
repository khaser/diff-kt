import output.*
import java.io.*
import java.io.File as JavaFile
import kotlin.test.*

internal class OutputMiscTests {

    private val standardOut = System.out
    private val stream = ByteArrayOutputStream()
    val textLine = "Some text for test"


    @BeforeTest
    fun setUp() {
        outputFile = null
        System.setOut(PrintStream(stream))
    }

    @AfterTest
    fun tearDown() {
        System.setOut(standardOut)
    }


    @Test
    fun testPrintLineEmptyLineToConsole() {
        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        setColor(Color.WHITE)
        println("")
        setColor(Color.WHITE)
        System.setOut(PrintStream(stream))
        printLine("")
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintLineToConsoleWithColor() {
        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        setColor(Color.GREEN)
        println(textLine)
        setColor(Color.WHITE)
        System.setOut(PrintStream(stream))
        printLine(textLine, Color.GREEN)
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintLineToConsoleInlineWithColor() {
        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        setColor(Color.GREEN)
        print(textLine)
        setColor(Color.WHITE)
        System.setOut(PrintStream(stream))
        printLine(textLine, Color.GREEN, PrintLineMode.INLINE)
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintLineToFile() {
        val fileName = "tests_files/FileWhichDoesNotExist"
        outputFile = JavaFile(fileName)
        outputFile!!.writeText("")
        printLine("Abracadabra")
        printLine("some rubbish here")
        val input = JavaFile(fileName).readLines()
        assertEquals("Abracadabra\nsome rubbish here", input.joinToString("\n"))
    }

    @Test
    fun testGetSingNone() {
        signMode = SignPrintingMode.LONG
        assertEquals("", getSign(0..100500, SignType.NONE))
        signMode = SignPrintingMode.NONE
        assertEquals("", getSign(0..100500, SignType.DELETED))
        assertEquals("", getSign(0..100500, SignType.ADDED))
        assertEquals("", getSign(0..100500, SignType.NONE))
    }

    @Test
    fun testGetSingLongAdded() {
        signMode = SignPrintingMode.LONG
        assertEquals("Added strings from 1 to 100501", getSign(0..100500, SignType.ADDED))
    }

    @Test
    fun testGetSingLongDeleted() {
        signMode = SignPrintingMode.LONG
        assertEquals("Deleted strings from 1 to 100501", getSign(0..100500, SignType.DELETED))
    }

    @Test
    fun testGetSingShortAdded() {
        signMode = SignPrintingMode.SHORT
        assertEquals("A 1-100501", getSign(0..100500, SignType.ADDED))
    }

    @Test
    fun testGetSingShortDeleted() {
        signMode = SignPrintingMode.SHORT
        assertEquals("D 1-100501", getSign(0..100500, SignType.DELETED))
    }

    @Test
    fun testPrintSeparator() {
        colWidth = 90
        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        setColor(Color.BLUE)
        println("".padEnd(colWidth * 2, '-'))
        setColor(Color.WHITE)
        System.setOut(PrintStream(stream))
        printSeparator()
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintBlock() {
        val fileName = "tests_files/TextA"
        val range = 5..18
        val block = JavaFile(fileName).readLines().slice(range).toTypedArray()

        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        for (i in block) {
            setColor(Color.PURPLE)
            println(i)
            setColor(Color.WHITE)
        }
        System.setOut(PrintStream(stream))
        printBlock(block, Color.PURPLE)
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintBlock2Columns() {
        val fileName = "tests_files/TextB"
        val rangeLeft = 5..18
        val rangeRight = 1..17
        val blockLeft = JavaFile(fileName).readLines().slice(rangeLeft).toTypedArray()
        val blockRight = JavaFile(fileName).readLines().slice(rangeRight).toTypedArray()

        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        for (i in blockRight.indices) {
            printLine2Columns(
                if (blockLeft.size > i) blockLeft[i] else "", blockRight[i],
                Pair(Color.PURPLE, Color.BLUE)
            )
        }
        System.setOut(PrintStream(stream))
        printBlock2Columns(blockLeft, blockRight, Pair(Color.PURPLE, Color.BLUE))
        assertEquals(correctStream.toString(), stream.toString())
    }
}

internal class OutputMethodsTests {

}