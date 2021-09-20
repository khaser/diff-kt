import input.Option
import input.parseAllKeys
import output.printDiff
import java.io.File
import kotlin.system.exitProcess


val help = """
NAME
    diff-kt - compare two files line by line on kotlin
SYNOPSIS
    java -jar diff-kt [OPTIONS] fileA fileB
OPTIONS
    [-h or --help] - show this help
    [-f ${'$'}FILE or --file ${'$'}FILE] - redirect output from standard output to ${'$'}FILE
    [-w ${'$'}NUM or --width ${'$'}NUM] - set column width to ${'$'}NUM
    [-s ${'$'}SIGN or --sign ${'$'}SIGN] - use short short sign of diff block instead of default long.
        ${'$'}SIGN can take the following values:
            long(Default)
            Example: Deleted strings from 24 to 28
            short
            Example: -24-28
            none Do not print sign
        Example: "-24-28" instead of "Deleted strings from 24 to 28"
    [-c ${'$'}MODE or --common ${'$'}MODE] set printing mode for common parts of files
    [-d ${'$'}MODE or --diff ${'$'}MODE] set printing mode for different parts of files
        ${'$'}MODE can take the following values:
            split(Default)
            Example: text from file A                        ||text from file B
            series
            Example: text from file a
                     text from file b
            none Nothing is output
    [-o or --context] Enable context mode for printing difference. This option ignoring --common and --diff
        Example:
        Common part 1
        text from file A 1                                   ||text from file B 1
        Common part 2
        -------------------------------------------------------------------------
        Common part 3
        text from file A 2                                   ||text from file B 2
        Common part 4
        -------------------------------------------------------------------------
    [-b ${'$'}NUM or --border ${'$'}NUM] Set context border to ${'$'}NUM lines. 5 by default.
EXIT CODES
    0 - SUCCESS code, files identical
    1 - SUCCESS code, files differ
    2 - FAIL code, Input\output failed
""".trimIndent()

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
    val argsKeys = args.slice(0..args.size - 3)
    //Parsing arguments with module input
    val options = parseAllKeys(argsKeys)

    if (options[Option.HELP] == "true") {
        print(help)
        return
    }

    val commonSubSec = CompareCore(fileNameA, fileNameB)

    printDiff(commonSubSec, options)
    if (commonSubSec.diff.size > 1) {
        exitProcess(1)
    } else {
        exitProcess(0)
    }
}
