import java.io.File
import java.lang.Integer.max

class CompareCore(fileNameA: String, fileNameB: String) {
    private val string2Int: MutableMap<String, Int> = mutableMapOf();
    private var int2String: Array<String> = arrayOf()
    val sequenceA = parseFile(fileNameA)
    val sequenceB = parseFile(fileNameB)
    val commonSequence = findLongestCommonSubSec()

    fun printString(sequence: Char, it: Int, color: String) {
        print(
            when (color) {
                "black" -> "\u001B[30m"
                "red" -> "\u001B[31m"
                "green" -> "\u001B[32m"
                "yellow" -> "\u001B[33m"
                "blue" -> "\u001B[34m"
                "purple" -> "\u001B[35m"
                "white" -> "\u001B[37m"
                else -> "\u001B[0m"
            }
        )

        println(
            int2String[when (sequence) {
                'A' -> sequenceA
                'B' -> sequenceB
                else -> throw Exception("Wrong sequence name. Only 'A' and 'B'")
            }[it]]
        )

        //Reset console output color
        print("\u001B[0m")
    }

    private fun parseFile(fileName: String): IntArray {
        val file = File(fileName)
        if (!file.exists()) throw Exception("Файл $fileName не найден. Проверьте путь и права доступа")

        val sequence: MutableList<Int> = mutableListOf()
        for (line in file.readLines()) {
            if (!string2Int.contains(line)) {
                sequence.add(string2Int.size)
                string2Int.put(line, string2Int.size)
            } else {
                sequence.add(string2Int[line]!!)
            }
        }
        int2String = string2Int.keys.toTypedArray()
        return sequence.toList().toIntArray()
    }

    private fun findLongestCommonSubSec(): ArrayList<IntArray> {
        val dp: Array<Array<Int>> = Array(sequenceA.size + 1) { Array(sequenceB.size + 1) { 0 } }

        for (i in 0..sequenceA.size) {
            for (j in 0..sequenceB.size) {
                if (i != 0) dp[i][j] = max(dp[i][j], dp[i - 1][j])
                if (j != 0) dp[i][j] = max(dp[i][j], dp[i][j - 1])
                if (i != 0 && j != 0 && sequenceA[i - 1] == sequenceB[j - 1]) dp[i][j] =
                    max(dp[i][j], dp[i - 1][j - 1] + 1)
            }
        }

        var itA = sequenceA.size;
        var itB = sequenceB.size;
        val commonSubSec: ArrayList<IntArray> = arrayListOf()
        while (itA != 0 || itB != 0) {
            if (itA != 0 && itB != 0 && dp[itA - 1][itB - 1] + 1 == dp[itA][itB] && sequenceA[itA - 1] == sequenceB[itB - 1]) {
                commonSubSec.add(intArrayOf(itA - 1, itB - 1))
                itA--;
                itB--;
            } else if (itA != 0 && dp[itA - 1][itB] == dp[itA][itB]) {
                itA--;
            } else {
                itB--;
            }
        }

        commonSubSec.reverse()
        return commonSubSec
    }

}

fun printAllNoSplit(core: CompareCore) {
    var itA = 0;
    var itB = 0;
    for (i in core.commonSequence) {
        for (it in itA until i[0]) {
            core.printString('A', it, "red")
        }
        itA = i[0]
        for (it in itB until i[1]) {
            core.printString('B', it, "green")
        }
        itB = i[1] + 1
        core.printString('A', itA++, "white")
    }

    for (it in itA until core.sequenceA.size) {
        core.printString('A', it, "red")
    }
    for (it in itB until core.sequenceB.size) {
        core.printString('B', it, "green")
    }
}


fun main(args: Array<String>) {
    if (args.size < 2) throw Exception("Необходимо указать два файла для сравнения")
    val argKeys = args.slice(0..args.size - 3)
    val fileNameA = args[args.size - 2]
    val fileNameB = args[args.size - 1]

    val shortKeys = args.filter { it.length == 2 && it[0] == '-' }.toSet()
//    val longKeys: Map<String, String> = args.reduceIndexed{index, acc, string -> }

//    shortKey.forEach { println(it) }
//    argKeys.forEach { println(it) }

    val commonSubSec = CompareCore(fileNameA, fileNameB)
    printAllNoSplit(commonSubSec)
}
