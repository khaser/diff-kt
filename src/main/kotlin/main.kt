import java.io.File
import output.*

fun parseLongArgs(args: List<String>): Map<String, String> {
    val longArgs: MutableMap<String, String> = mutableMapOf()
    var localArgs = args.toMutableList()
    while (args.isNotEmpty()) {
        localArgs = localArgs.dropWhile { !(it.length > 2 && it.slice(0..1) == "--") }.toMutableList()
        if (localArgs.isEmpty()) break
        if (localArgs.size == 1) throw Exception("After --parameter_name must be value")
        longArgs[localArgs[0].drop(2)] = localArgs[1]
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
    when (longKeys["view"]) {
        "1" -> printAll(commonSubSec, PrintingMode.SERIES, PrintingMode.SERIES)
        "2" -> printAll(commonSubSec, PrintingMode.SERIES, PrintingMode.SPLIT)
        "3" -> printWithBorder(commonSubSec, 4)
    }
//    when (longKeys["view"]) {
//        "1" -> printAllSplit(commonSubSec)
//        "2" -> printOnlyDiffSeries(commonSubSec)
//        "3" -> printOnlyDiffSplit(commonSubSec)
//        else -> printOnlyDiffSeries(commonSubSec)
//    }
}
