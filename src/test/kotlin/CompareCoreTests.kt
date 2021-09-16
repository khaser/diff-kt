import kotlin.test.*

internal class CompareCoreTests {


    val fileNameA = "tests_files/TextA"
    val fileNameB = "tests_files/TextB"
    val core = CompareCore(fileNameA, fileNameB)

    fun checkDiff() {
        val diff = core.diff
        for (block in diff) {
            if (block.blockA === block.blockB) {
                for (i in block.blockA.seg) {
                    assert(core.commonSequence.find { it.first == i } != null)
                }
            } else {
                assert(!block.blockA.text.contentDeepEquals(block.blockB.text))
            }
        }
    }

    @Test
    fun testCore1() {
        core.fileA.size = 6
        core.fileB.size = 6
        core.fileA.sequence = intArrayOf(1, 2, 3, 4, 5, 6)
        core.fileB.sequence = intArrayOf(1, 2, 3, 4, 5, 6)
        val correctSubSec: ArrayList<Pair<Int, Int>> = arrayListOf(
            Pair(0, 0), Pair(1, 1),
            Pair(2, 2), Pair(3, 3), Pair(4, 4), Pair(5, 5)
        )
        assertEquals(correctSubSec, core.findLongestCommonSubSec())
    }

    @Test
    fun testCore2() {
        core.fileA.size = 6
        core.fileB.size = 4
        core.fileA.sequence = intArrayOf(1, 1, 1, 1, 1, 1)
        core.fileB.sequence = intArrayOf(1, 2, 3, 1)
        val correctSubSec: ArrayList<Pair<Int, Int>> = arrayListOf(Pair(0, 0), Pair(5, 3))
        assertEquals(correctSubSec, core.findLongestCommonSubSec())
    }

    @Test
    fun testCore3() {
        core.fileA.size = 0
        core.fileB.size = 2
        core.fileA.sequence = intArrayOf()
        core.fileB.sequence = intArrayOf(1, 1)
        val correctSubSec: ArrayList<Pair<Int, Int>> = arrayListOf()
        assertEquals(correctSubSec, core.findLongestCommonSubSec())
    }

    @Test
    fun testCore4() {
        core.fileA.size = 7
        core.fileB.size = 5
        core.fileA.sequence = intArrayOf(2, 6, 1, 5, 1, 2, 6)
        core.fileB.sequence = intArrayOf(6, 5, 0, 2, 6)
        val correctSubSec: ArrayList<Pair<Int, Int>> = arrayListOf(Pair(1, 0), Pair(3, 1), Pair(5, 3), Pair(6, 4))
        assertEquals(correctSubSec, core.findLongestCommonSubSec())
    }
}

internal class FileTests {

    val fileName = "tests_files/TextA"
    var commonMap: MutableMap<String, Int> = mutableMapOf()
    var file = File(fileName, commonMap)

    @BeforeTest
    fun setUp() {
        commonMap = mutableMapOf()
        file = File(fileName, commonMap)
    }

    @Test
    fun testFile() {
        for (i in file.int2String) {
            assert(file.int2String[file.string2Int[i]!!] == i)
        }
    }

    @Test
    fun testTwoFiles() {
        val fileB = File("tests_files/TextB", commonMap)
        for (i in file.int2String) {
            assert(file.int2String[file.string2Int[i]!!] == i)
        }
        for (i in fileB.int2String) {
            assert(fileB.int2String[fileB.string2Int[i]!!] == i)
        }
        assert(fileB.string2Int === file.string2Int)
        file.getBlock(14, 19).contentEquals(fileB.getBlock(0, 5))
        assertEquals(file.getString(15), fileB.getString(1))
    }
}