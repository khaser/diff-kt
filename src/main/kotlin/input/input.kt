package input

import output.*

//keys - strings like --help
//args - string after keys

enum class Option(val longKey: String, val shortKey: String) {
    HELP("--help", "-h"),
    FILE("--file", "-f"),
    WIDTH("--width", "-w"),
    SIGN_MODE("--sign", "-s"),
    COMMON_MODE("--common", "-c"),
    DIFF_MODE("--diff", "-d"),
    ENABLE_CONTEXT("--context", "-o"),
    CONTEXT_BORDER("--border", "-b")
}

val ArgOptions: Set<Option> = setOf(Option.FILE, Option.WIDTH, Option.SIGN_MODE, Option.COMMON_MODE, Option.DIFF_MODE, Option.CONTEXT_BORDER)
val noArgOptions: Set<Option> = setOf(Option.HELP, Option.ENABLE_CONTEXT)

val keyShortcut = Option.values().map {Pair(it.shortKey, it.longKey)}.toMap()
val keyOption = Option.values().map {Pair(it.longKey, it)}.toMap()

//Parse all user input, main function of package
fun parseArgs(args: List<String>): Map<Option, String> {
    val result: MutableMap<Option, String> = mutableMapOf()
    //cast all short keys to long keys
    val normalizedArgs = args.map { if (keyShortcut.containsKey(it)) keyShortcut[it]!! else it }
    val (onlyKey, keyWithArg) = normalizedArgs.partition { noArgOptions.contains(keyOption[it]) }
    //parse keys without args
    noArgOptions.forEach { result[it] = onlyKey.contains(it.longKey).toString() }
    //parse keys with args
    parseKeysWithArgs(keyWithArg as ArrayList<String>, result)
    return result
}


//Function for parsing options with argument
private fun parseKeysWithArgs(args: ArrayList<String>, result: MutableMap<Option, String>) {
    val dropped: MutableList<String> = mutableListOf()
    while (args.isNotEmpty()) {
        dropped.addAll(args.takeWhile { !ArgOptions.contains(keyOption[it]) }.also { repeat(it.size) {args.removeFirst()} })
        if (args.isEmpty()) break
        if (args.size == 1) {
            println("Warning!!! After ${args[0]} option must be value")
            dropped.add(args[0])
            break
        }
        with (keyOption[args[0]]!!) {
            if (result.containsKey(this)) {
                println("Warning!!! Redeclaration of option ${args[0]}")
            }
            result[this] = args[1]
            repeat(2) { args.removeFirst() }
        }
    }
    if (dropped.isNotEmpty()) println("Was ignored next keys: ${dropped.joinToString(" ")}")
}


//Convert string argument to enum
fun keyMathing(options: Map<Option, String>) {
    signMode = when (options[Option.SIGN_MODE]) {
        "long",null -> SignPrintingMode.LONG
        "short" -> SignPrintingMode.SHORT
        "none" -> SignPrintingMode.NONE
        else -> {
            println("Warning!!! Mode \"${options[Option.SIGN_MODE]}\" for option --sign is incorrect. Using default mode - long");
            SignPrintingMode.LONG
        }
    }
    commonMode = when (options[Option.COMMON_MODE]) {
        "split",null -> PrintingMode.SPLIT
        "series" -> PrintingMode.SERIES
        "none" -> PrintingMode.NONE
        else -> {
            println("Warning!!! Mode \"${options[Option.COMMON_MODE]}\" for option --common is incorrect. Using default mode - split");
            PrintingMode.SPLIT
        }
    }
    diffMode = when (options[Option.DIFF_MODE]) {
        "split",null -> PrintingMode.SPLIT
        "series" -> PrintingMode.SERIES
        "none" -> PrintingMode.NONE
        else -> {
            println("Warning!!! Mode \"${options[Option.DIFF_MODE]}\" for option --diff is incorrect. Using default mode - split");
            PrintingMode.SPLIT
        }
    }
}