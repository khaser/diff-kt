import kotlin.system.exitProcess
import java.io.File as JavaFile

//Class for reading input file and converting it to sequence of integers(further SID) saving return transformation.
class File(fileName: String, commonMap: MutableMap<String, Int>) {

    //Map for getting string by SID
    val string2Int = commonMap

    //Array reverse to string2Int. Used to give equal strings from different files the same SID
    var int2String: List<String> = listOf()

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
        input.forEachLine {
            if (!string2Int.contains(it)) {
                sequence.add(string2Int.size)
                string2Int[it] = string2Int.size
            } else {
                sequence.add(string2Int[it]!!)
            }
        }
        int2String = string2Int.keys.toList()
        minWidth = int2String.maxOf { it.length }
        return sequence.toList().toIntArray()
    }

    fun getBlock(range: IntRange): List<String> {
        return range.map{ getString(it) }
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
        val text = file.getBlock(range)
        val seg = range
        val size = range.last - range.first + 1
        val width = file.minWidth
    }

    data class DiffBlock(val blockA: TextBlock, val blockB: TextBlock)

    val diff: MutableList<DiffBlock> = generateDiff()

    //Generate diff object from commonSequence
    private fun generateDiff(): ArrayList<DiffBlock> {
        var lastTaken = Pair(-1, -1)
        val result: ArrayList<DiffBlock> = arrayListOf()
                while (commonSequence.isNotEmpty()) {
            //take common block
            commonSequence.takeWhile { if (it.first - lastTaken.first == 1 && it.second - lastTaken.second == 1)
                {lastTaken=it; true} else false}
                .also {
                TextBlock(fileA, it[0].first..it.last().first).let{result.add(DiffBlock(it, it))}
                repeat(it.size) {commonSequence.removeFirst()}
            }
            if (commonSequence.isEmpty()) break;
            //take diff block
            val textFromA = TextBlock(fileA, lastTaken.first + 1 until commonSequence[0].first)
            val textFromB = TextBlock(fileB, lastTaken.second + 1 until commonSequence[0].second)
            result.add(DiffBlock(textFromA, textFromB))
            lastTaken = commonSequence[0].let{Pair(it.first - 1, it.second - 1)}
        }
        val textFromA = TextBlock(fileA, lastTaken.first + 1 until fileA.size)
        val textFromB = TextBlock(fileB, lastTaken.second + 1 until fileB.size)
        result.add(DiffBlock(textFromA, textFromB))
        return result
    }

    //calc dynamic programming on files sequences and return longest common sequence
    fun findLongestCommonSubSec(): ArrayList<Pair<Int, Int>> {
        val dp: List<IntArray> = List(fileA.size + 1) { IntArray(fileB.size + 1) }

        for (i in 0..fileA.size) {
            for (j in 0..fileB.size) {
                if (i != 0) dp[i][j] = Integer.max(dp[i][j], dp[i - 1][j])
                if (j != 0) dp[i][j] = Integer.max(dp[i][j], dp[i][j - 1])
                if (i != 0 && j != 0 && fileA.sequence[i - 1] == fileB.sequence[j - 1])
                    dp[i][j] = Integer.max(dp[i][j], dp[i - 1][j - 1] + 1)
            }
        }

        return reverseDpPropogaration(fileA.sequence.size, fileB.sequence.size, dp)
    }

    //get common sequence from calculated dynamic programming
    private fun reverseDpPropogaration(indexA: Int, indexB: Int, dp:List<IntArray>): ArrayList<Pair<Int, Int>> {
        if (indexA == 0 || indexB == 0) return ArrayList(0)

        if (dp[indexA - 1][indexB - 1] + 1 == dp[indexA][indexB] && fileA.sequence[indexA - 1] == fileB.sequence[indexB - 1]) {
            //return recursion answer with new common element
            reverseDpPropogaration(indexA - 1, indexB - 1, dp).let{it.add(Pair(indexA - 1, indexB - 1)); return it}
        }

        //return the best recursion answer without new common element
        return if (dp[indexA - 1][indexB] == dp[indexA][indexB]) {
            reverseDpPropogaration(indexA - 1, indexB, dp)
        } else {
            reverseDpPropogaration(indexA, indexB - 1, dp)
        }
    }
}