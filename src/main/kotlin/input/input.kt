package input

import output.*

enum class Options {
    HELP, FILE, WIDTH, SIGN_MODE, COMMON_MODE, DIFF_MODE, ENABLE_CONTEXT, CONTEXT_BORDER
}

//Function for parsing options with argument
private fun parseLongArgs(args: MutableList<String>, result: MutableMap<Options, String>) {
    val keys: Map<String, Options> = mapOf(
        Pair("-f", Options.FILE), Pair("-w", Options.WIDTH), Pair("-c", Options.COMMON_MODE),
        Pair("-d", Options.DIFF_MODE), Pair("-b", Options.CONTEXT_BORDER), Pair("-s", Options.SIGN_MODE),
        Pair("--file", Options.FILE), Pair("--width", Options.WIDTH), Pair("--common", Options.COMMON_MODE),
        Pair("--diff", Options.DIFF_MODE), Pair("--border", Options.CONTEXT_BORDER), Pair("--sign", Options.SIGN_MODE)
    )

    val dropped: MutableList<String> = mutableListOf()
    while (args.isNotEmpty()) {
        while (args.isNotEmpty() && !keys.containsKey(args[0])) {
            dropped.add(args[0])
            args.removeFirst()
        }
        if (args.isEmpty()) break
        if (args.size == 1) {
            println("Warning!!! After ${args[0]} option must be value")
            dropped.add(args[0])
            break
        }
        if (result.containsKey(keys[args[0]])) {
            println("Warning!!! Redeclaration of option ${args[0]}")
        }
        result[keys[args[0]]!!] = args[1]
        repeat(2) { args.removeFirst() }
    }
    if (dropped.isNotEmpty()) println("Was ignored next keys: ${dropped.joinToString(" ")}")
}

//Parse all options, main function of package
fun parseArgs(args: MutableList<String>): Map<Options, String> {
    val result: MutableMap<Options, String> = mutableMapOf()
    val flagKeys: Set<String> = setOf("-h", "-o", "--help", "--context")

    val flagArgs = args.filter { flagKeys.contains(it) }
    args.removeIf { flagKeys.contains(it) }
    if (flagArgs.contains("-h") || flagArgs.contains("--help"))
        result[Options.HELP] = "True"
    if (flagArgs.contains("-o") || flagArgs.contains("--context"))
        result[Options.ENABLE_CONTEXT] = "True"
    parseLongArgs(args, result)
    return result
}

//Convert string argument to enum
fun keyMathing(options: Map<Options, String>) {
    signMode = when (options[Options.SIGN_MODE]) {
        "long" -> SignPrintingMode.LONG
        "short" -> SignPrintingMode.SHORT
        "none" -> SignPrintingMode.NONE
        null -> SignPrintingMode.LONG
        else -> {
            println("Warning!!! Mode \"${options[Options.SIGN_MODE]}\" for option --sign is incorrect. Using default mode - long");
            SignPrintingMode.LONG
        }
    }
    commonMode = when (options[Options.COMMON_MODE]) {
        "split" -> PrintingMode.SPLIT
        "series" -> PrintingMode.SERIES
        "none" -> PrintingMode.NONE
        null -> PrintingMode.SPLIT
        else -> {
            println("Warning!!! Mode \"${options[Options.COMMON_MODE]}\" for option --common is incorrect. Using default mode - split");
            PrintingMode.SPLIT
        }
    }
    diffMode = when (options[Options.DIFF_MODE]) {
        "split" -> PrintingMode.SPLIT
        "series" -> PrintingMode.SERIES
        "none" -> PrintingMode.NONE
        null -> PrintingMode.SPLIT
        else -> {
            println("Warning!!! Mode \"${options[Options.DIFF_MODE]}\" for option --diff is incorrect. Using default mode - split");
            PrintingMode.SPLIT
        }
    }
}