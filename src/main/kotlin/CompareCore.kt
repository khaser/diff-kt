import kotlin.system.exitProcess
import java.io.File as JavaFile

//Class for reading input file and converting it to sequence of integers(further SID) saving return transformation.
class File(fileName: String, commonMap: MutableMap<String, Int>) {

    //Map for getting string by SID
    val string2Int = commonMap

    //Array reverse to string2Int. Used to give equal strings from different files the same SID
    var int2String: Array<String> = arrayOf()

    //Array of SID for file
    var sequence = parseFile(fileName)
    var size = sequence.size
    var minWidth = 0

    private fun parseFile(fileName: String): IntArray {
        val input = JavaFile(fileName)
        if (!input.exists()) {
            println("Файл $fileName не найден. Проверьте путь и права доступа")
            exitProcess(2)
        }

        val sequence: MutableList<Int> = mutableListOf()
        for (line in input.readLines()) {
            if (!string2Int.contains(line)) {
                sequence.add(string2Int.size)
                string2Int[line] = string2Int.size
            } else {
                sequence.add(string2Int[line]!!)
            }
        }
        int2String = string2Int.keys.toTypedArray()
        minWidth = int2String.maxOf { it.length }
        return sequence.toList().toIntArray()
    }

    fun getBlock(from: Int, to: Int): Array<String> {
        return Array(to - from + 1) { getString(it + from) }
    }

    fun getString(index: Int) = int2String[sequence[index]]
}

class CompareCore(fileNameA: String, fileNameB: String) {

    //Common SID -> String map for both files
    val string2Int: MutableMap<String, Int> = mutableMapOf()
    val fileA = File(fileNameA, string2Int)
    val fileB = File(fileNameB, string2Int)

    //Longest common sequence as SIDs array
    val commonSequence = findLongestCommonSubSec()

    //Next classes used for easy access to part of output
    class TextBlock(file: File, range: IntRange) {
        val text = file.getBlock(range.first, range.last)
        val seg = range
        val size = range.last - range.first + 1
        val width = file.minWidth
    }

    data class DiffBlock(val blockA: TextBlock, val blockB: TextBlock)

    val diff: MutableList<DiffBlock> = generateDiff()

    //Generate diff object from commonSequence
    private fun generateDiff(): MutableList<DiffBlock> {
        var alreadyAddedFromA = 0
        var alreadyAddedFromB = 0
        var lastCommon = Pair(-1, -1)
        val result: MutableList<DiffBlock> = mutableListOf()
        for (i in commonSequence) {
            if ((i.first - lastCommon.first) != 1 || (i.second - lastCommon.second) != 1) {
                if (alreadyAddedFromA <= lastCommon.first) {
                    val commonPart = TextBlock(fileA, alreadyAddedFromA..lastCommon.first)
                    alreadyAddedFromA = lastCommon.first + 1
                    alreadyAddedFromB = lastCommon.second + 1
                    result.add(DiffBlock(commonPart, commonPart))
                }

                val partFromA = TextBlock(fileA, alreadyAddedFromA until i.first)
                val partFromB = TextBlock(fileB, alreadyAddedFromB until i.second)
                alreadyAddedFromA = i.first
                alreadyAddedFromB = i.second
                result.add(DiffBlock(partFromA, partFromB))
            }
            lastCommon = i
        }
        if (alreadyAddedFromA <= lastCommon.first) {
            val commonPart = TextBlock(fileA, alreadyAddedFromA..lastCommon.first)
            result.add(DiffBlock(commonPart, commonPart))
        }
        return result
    }


    fun findLongestCommonSubSec(): ArrayList<Pair<Int, Int>> {
        val dp: Array<Array<Int>> = Array(fileA.size + 1) { Array(fileB.size + 1) { 0 } }

        for (i in 0..fileA.size) {
            for (j in 0..fileB.size) {
                if (i != 0) dp[i][j] = Integer.max(dp[i][j], dp[i - 1][j])
                if (j != 0) dp[i][j] = Integer.max(dp[i][j], dp[i][j - 1])
                if (i != 0 && j != 0 && fileA.sequence[i - 1] == fileB.sequence[j - 1]) dp[i][j] =
                    Integer.max(dp[i][j], dp[i - 1][j - 1] + 1)
            }
        }

        var itA = fileA.size
        var itB = fileB.size
        val commonSubSec: ArrayList<Pair<Int, Int>> = arrayListOf()
        while (itA != 0 || itB != 0) {
            if (itA != 0 && itB != 0 && dp[itA - 1][itB - 1] + 1 == dp[itA][itB] && fileA.sequence[itA - 1] == fileB.sequence[itB - 1]) {
                commonSubSec.add(Pair(itA - 1, itB - 1))
                itA--
                itB--
            } else if (itA != 0 && dp[itA - 1][itB] == dp[itA][itB]) {
                itA--
            } else {
                itB--
            }
        }

        commonSubSec.reverse()
        return commonSubSec
    }
}