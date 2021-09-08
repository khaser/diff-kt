import java.io.File
import java.lang.Integer.max
import java.util.function.Function

class CompareCore(fileNameA: String, fileNameB: String) {
    private val string2Int: MutableMap<String, Int> = mutableMapOf();
    private var int2String: Array<String> = arrayOf()
    val sequenceA = parseFile(fileNameA)
    val sequenceB = parseFile(fileNameB)
    val commonSequence = findLongestCommonSubSec()

    fun getString(sequence: Char, it: Int) = int2String[ when (sequence) {
        'A' -> sequenceA
        'B' -> sequenceB
        else -> throw Exception("Wrong sequence name. Only 'A' and 'B'")
    }[it]]

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

fun setColor(color: String) {
    print(
        when (color) {
            "black" -> "\u001B[30m"
            "red" -> "\u001B[31m"
            "green" -> "\u001B[32m"
            "yellow" -> "\u001B[33m"
            "blue" -> "\u001B[34m"
            "purple" -> "\u001B[35m"
            "gray" -> "\u001B[37m"
            else -> "\u001B[0m"
        }
    )
}

fun print2Colomns(strA: String, strB: String, colWidth: Int = 120) {
    setColor("red")
    print("${strA.padEnd(colWidth)}||")
    setColor("green")
    println(strB)
}

fun printAllSplit(core: CompareCore) {
    var itA = 0;
    var itB = 0;
    val getA: (Int) -> String = { core.getString('A', it) }
    val getB: (Int) -> String = { core.getString('B', it) }

    for (i in core.commonSequence) {
        repeat(max(i[0] - itA, i[1] - itB)) {
            print2Colomns(if (itA < i[0]) getA(itA) else "", if (itB < i[1]) getB(itB) else "");
            itA++
            itB++;
        }
        itA = i[0]
        itB = i[1] + 1
        printString(core.getString('A', itA++), "white")
    }

    repeat(max(core.sequenceA.size - itA, core.sequenceB.size - itB)) {
        print2Colomns(if (itA < core.sequenceA.size) getA(itA) else "", if (itB < core.sequenceB.size) getB(itB) else "");
        itA++;
        itB++;
    }
}

fun printString(str: String, color: String) {
    setColor(color)
    println(str)
    //Reset console output color
    setColor("")
}

fun printAllNoSplit(core: CompareCore) {
    var itA = 0;
    var itB = 0;
    val printA: (Int) -> Unit = { printString(core.getString('A', it), "red")}
    val printB: (Int) -> Unit = { printString(core.getString('B', it), "green")}

    for (i in core.commonSequence) {
        for (it in itA until i[0]) {
            printA(it)
        }
        itA = i[0]
        for (it in itB until i[1]) {
            printB(it)
        }
        itB = i[1] + 1
        printString(core.getString('A', itA++), "white")
    }

    for (it in itA until core.sequenceA.size) {
        printA(it)
    }
    for (it in itB until core.sequenceB.size) {
        printB(it)
    }
}

fun parseLongArgs(args: List<String>): Map<String, String> {
    val longArgs: MutableMap<String, String> = mutableMapOf()
    var localArgs = args.toMutableList()
    while (!args.isEmpty()) {
        localArgs = localArgs.dropWhile { !(it.length > 2 && it.slice(0..1) == "--") }.toMutableList()
        if (localArgs.isEmpty()) break
        if (localArgs.size == 1) throw Exception("After --parameter_name must be value")
        longArgs.put(localArgs[0].drop(2), localArgs[1])
        localArgs.removeFirst()
    }
    return longArgs
}



fun main(args: Array<String>) {
    if (args.size < 2) throw Exception("Необходимо указать два файла для сравнения")
    val argsKeys = args.slice(0..args.size - 3)
    val fileNameA = args[args.size - 2]
    val fileNameB = args[args.size - 1]

    val shortKeys = argsKeys.filter { it.length == 2 && it[0] == '-' }.map { it[1] }.toSet()
    val longKeys = parseLongArgs(argsKeys)

    if (shortKeys.contains('h')) {
        val helpFile = File("help.txt")
        helpFile.readLines().forEach{println(it)}
        return
    }

    val commonSubSec = CompareCore(fileNameA, fileNameB)
    when (longKeys.get("view")) {
        "simple" -> printAllNoSplit(commonSubSec)
        "split_full" -> printAllSplit(commonSubSec)
        "split_short" -> printAllNoSplit(commonSubSec)
        else -> printAllNoSplit(commonSubSec)
    }
}
