import java.io.File
import input.*
import output.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    //Preparsing argument
    if (args.size < 2) {
        if (args.size == 1 && (args[0] == "-h" || args[0] == "--help")) {
            val helpFile = File("help.txt")
            helpFile.readLines().forEach { println(it) }
        } else {
            println("You must specify 2 files for comparison")
            exitProcess(2)
        }
        return
    }
    val fileNameA = args[args.size - 2]
    val fileNameB = args[args.size - 1]
    val argsKeys = args.slice(0..args.size - 3).toMutableList()
    //Parsing arguments with module input
    val options = parseArgs(argsKeys)

    if (options.contains(Options.HELP)) {
        val helpFile = File("help.txt")
        helpFile.readLines().forEach { println(it) }
        return
    }

    val commonSubSec = CompareCore(fileNameA, fileNameB)

    printDiff(commonSubSec, options)
    if (commonSubSec.diff.size > 1) {
        exitProcess(1);
    } else {
        exitProcess(0);
    }
}
