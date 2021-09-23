import kotlin.system.exitProcess
import java.io.File as JavaFile

typealias SID = Int

/** Class for reading input file and converting it to sequence of integers(further SID) saving return transformation.*/
class File(fileName: String, commonMap: MutableMap<String, SID>) {

    /**Map for getting string by SID*/
    val string2Int = commonMap

    /**Array reverse to string2Int. Used to give equal strings from different files the same SID*/
    var int2String: List<String> = listOf()

    /**Array of SID for file*/
    var sequence = parseFile(fileName)
    var size = sequence.size
    var minWidth = 0

    private fun parseFile(fileName: String): IntArray {
        val input = JavaFile(fileName)
        if (!input.exists()) {
            println("Файл $fileName не найден. Проверьте путь и права доступа")
            exitProcess(2)
        }

        val sequence: MutableList<SID> = mutableListOf()
        input.forEachLine {
            with(it.replace("\t", "    ")) {
                if (!string2Int.contains(this)) {
                    sequence.add(string2Int.size)
                    string2Int[this] = string2Int.size
                } else {
                    sequence.add(string2Int[this]!!)
                }
            }
        }
        int2String = string2Int.keys.toList()
        minWidth = int2String.maxOf { it.length }
        return sequence.toList().toIntArray()
    }

    fun getBlock(range: IntRange): List<String> {
        return range.map { getString(it) }
    }

    fun getString(index: SID) = int2String[sequence[index]]
}

/**Class for creating diff object from two Files*/
class CompareCore(fileNameA: String, fileNameB: String) {

    /**Common SID -> String map for both files*/
    private val string2Int: MutableMap<String, SID> = mutableMapOf()
    val fileA = File(fileNameA, string2Int)
    val fileB = File(fileNameB, string2Int)

    /**Longest common sequence as SIDs array*/
    private val commonSequence = findLongestCommonSubSec()

    /**Next classes used for easy access to part of output*/
    class TextBlock(file: File, range: IntRange) {
        val text = file.getBlock(range)
        val seg = range
        val size = range.last - range.first + 1
        val width = file.minWidth
    }

    data class DiffBlock(val blockA: TextBlock, val blockB: TextBlock)

    val diff: MutableList<DiffBlock> = generateDiff()

    /**Generate diff object from commonSequence*/
    private fun generateDiff(): ArrayList<DiffBlock> {
        var lastTaken = Pair(-1, -1)
        val result: ArrayList<DiffBlock> = arrayListOf()
        while (commonSequence.isNotEmpty()) {

            //Take common block
            val commonBlock = commonSequence.takeWhile {
                if (it.first - lastTaken.first == 1 && it.second - lastTaken.second == 1) {
                    lastTaken = it; true
                } else false
            }
            with(commonBlock) {
                if (this.isNotEmpty()) {
                    TextBlock(fileA, this[0].first..this.last().first).let { result.add(DiffBlock(it, it)) }
                    repeat(this.size) { commonSequence.removeFirst() }
                }
            }
            if (commonSequence.isEmpty()) break

            //Take diff block
            val textFromA = TextBlock(fileA, lastTaken.first + 1 until commonSequence[0].first)
            val textFromB = TextBlock(fileB, lastTaken.second + 1 until commonSequence[0].second)
            result.add(DiffBlock(textFromA, textFromB))
            lastTaken = commonSequence[0].let { Pair(it.first - 1, it.second - 1) }
        }
        //Take all strings after last common block
        val textFromA = TextBlock(fileA, lastTaken.first + 1 until fileA.size)
        val textFromB = TextBlock(fileB, lastTaken.second + 1 until fileB.size)
        if (textFromA.size > 0 || textFromB.size > 0) result.add(DiffBlock(textFromA, textFromB))
        return result
    }

    /**Calculate dynamic programming on files sequences and return longest common sequence*/
    fun findLongestCommonSubSec(): ArrayList<Pair<SID, SID>> {
        val dp: List<IntArray> = List(fileA.size + 1) { IntArray(fileB.size + 1) }

        for (i in 0..fileA.size) {
            for (j in 0..fileB.size) {
                if (i != 0) dp[i][j] = Integer.max(dp[i][j], dp[i - 1][j])
                if (j != 0) dp[i][j] = Integer.max(dp[i][j], dp[i][j - 1])
                if (i != 0 && j != 0 && fileA.sequence[i - 1] == fileB.sequence[j - 1])
                    dp[i][j] = Integer.max(dp[i][j], dp[i - 1][j - 1] + 1)
            }
        }
        return reverseDpPropagation(fileA.size, fileB.size, dp)
    }

    /**Get common sequence from calculated dynamic programming*/
    private fun reverseDpPropagation(itA: Int, itB: Int, dp: List<IntArray>): ArrayList<Pair<SID, SID>> {
        if (itA == 0 || itB == 0) return arrayListOf()
        if (dp[itA - 1][itB - 1] + 1 == dp[itA][itB] && fileA.sequence[itA - 1] == fileB.sequence[itB - 1]) {
            return reverseDpPropagation(itA - 1, itB - 1, dp).also { it.add(Pair(itA - 1, itB - 1)) }
        }
        return if (dp[itA - 1][itB] == dp[itA][itB]) {
            reverseDpPropagation(itA - 1, itB, dp)
        } else {
            reverseDpPropagation(itA, itB - 1, dp)
        }
    }
}