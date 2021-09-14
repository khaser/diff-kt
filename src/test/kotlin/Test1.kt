import output.colWidth
import java.io.*
import kotlin.test.*

internal class OutputMiscTests {

    private val standardOut = System.out
    private val stream = ByteArrayOutputStream()
    val textLine = "Some text for test"


    @BeforeTest
    fun setUp() {
        output.outputFile = null
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
        output.setColor(output.Color.WHITE)
        println("")
        output.setColor(output.Color.WHITE)
        System.setOut(PrintStream(stream))
        output.printLine("")
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintLineToConsoleWithColor() {
        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        output.setColor(output.Color.GREEN)
        println(textLine)
        output.setColor(output.Color.WHITE)
        System.setOut(PrintStream(stream))
        output.printLine(textLine, output.Color.GREEN)
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintLineToConsoleInlineWithColor() {
        val correctStream = ByteArrayOutputStream()
        System.setOut(PrintStream(correctStream))
        output.setColor(output.Color.GREEN)
        print(textLine)
        output.setColor(output.Color.WHITE)
        System.setOut(PrintStream(stream))
        output.printLine(textLine, output.Color.GREEN, output.PrintLineMode.INLINE)
        assertEquals(correctStream.toString(), stream.toString())
    }

    @Test
    fun testPrintLineToFile() {
        output.outputFile = File("FileWhichDoesNotExist")
        output.outputFile!!.writeText("")
        output.printLine("Abracadabra")
        output.printLine("some rubbish here")
        val input = File("FileWhichDoesNotExist").readLines()
        assertEquals("Abracadabra\nsome rubbish here", input.joinToString("\n"))
    }

    @Test
    fun testGetSingNone() {
        output.signMode = output.SignPrintingMode.LONG
        assertEquals("", output.getSign(CompareCore.Segment(0, 100500), output.SignType.NONE))
        output.signMode = output.SignPrintingMode.NONE
        assertEquals("", output.getSign(CompareCore.Segment(0, 100500), output.SignType.DELETED))
        assertEquals("", output.getSign(CompareCore.Segment(0, 100500), output.SignType.ADDED))
        assertEquals("", output.getSign(CompareCore.Segment(0, 100500), output.SignType.NONE))
    }

    @Test
    fun testGetSingLongAdded() {
        output.signMode = output.SignPrintingMode.LONG
        assertEquals("Added strings from 1 to 100501", output.getSign(CompareCore.Segment(0, 100500), output.SignType.ADDED))
    }

    @Test
    fun testGetSingLongDeleted() {
        output.signMode = output.SignPrintingMode.LONG
        assertEquals("Deleted strings from 1 to 100501", output.getSign(CompareCore.Segment(0, 100500), output.SignType.DELETED))
    }

    @Test
    fun testGetSingShortAdded() {
        output.signMode = output.SignPrintingMode.SHORT
        assertEquals("A 1-100501", output.getSign(CompareCore.Segment(0, 100500), output.SignType.ADDED))
    }

    @Test
    fun testGetSingShortDeleted() {
        output.signMode = output.SignPrintingMode.SHORT
        assertEquals("D 1-100501", output.getSign(CompareCore.Segment(0, 100500), output.SignType.DELETED))
    }

    @Test
    fun testPrintSeparator() {
        val correctStream = ByteArrayOutputStream()
        colWidth = 90
        System.setOut(PrintStream(correctStream))
        output.setColor(output.Color.BLUE)
        println("".padEnd(colWidth * 2, '-'))
        output.setColor(output.Color.WHITE)
        System.setOut(PrintStream(stream))
        output.printSeparator()
        assertEquals(correctStream.toString(), stream.toString())
    }

}
