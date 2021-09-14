package output
import CompareCore
import input.Options


var colWidth = 80
var signMode = SignPrintingMode.LONG

fun printDiff(core: CompareCore, options: Map<Options, String>) {

    val minWidth = core.diff.maxOf { it.blockA.width }

    colWidth = options[Options.WIDTH]?.toIntOrNull() ?: minWidth


    signMode = when(options[Options.SIGN_MODE]) {
        "long" -> SignPrintingMode.LONG
        "short" -> SignPrintingMode.SHORT
        "none" -> SignPrintingMode.NONE
        null -> SignPrintingMode.LONG
        else -> {println("Warning!!! Mode \"${options[Options.SIGN_MODE]}\" for option --sign is incorrect. Using default mode - long");
            SignPrintingMode.LONG}
    }

    val commonMode: PrintingMode = when(options[Options.COMMON_MODE]) {
        "split" -> PrintingMode.SPLIT
        "series" -> PrintingMode.SERIES
        "none" -> PrintingMode.NONE
        null -> PrintingMode.SPLIT
        else -> {println("Warning!!! Mode \"${options[Options.COMMON_MODE]}\" for option --common is incorrect. Using default mode - split");
            PrintingMode.SPLIT}
    }
    val diffMode: PrintingMode = when(options[Options.DIFF_MODE]) {
        "split" -> PrintingMode.SPLIT
        "series" -> PrintingMode.SERIES
        "none" -> PrintingMode.NONE
        null -> PrintingMode.SPLIT
        else -> {println("Warning!!! Mode \"${options[Options.DIFF_MODE]}\" for option --diff is incorrect. Using default mode - split");
            PrintingMode.SPLIT}
    }
    if (options.containsKey(Options.ENABLE_CONTEXT)) {
        printWithBorder(core, options[Options.CONTEXT_BORDER]?.toIntOrNull() ?: 5)
    } else {
        printAll(core, commonMode, diffMode)
    }
}

fun printAll(core: CompareCore, commonMode: PrintingMode, diffMode: PrintingMode) {
    for (i in core.diff) {
        if (i.blockA === i.blockB) {
            when (commonMode) {
                PrintingMode.SPLIT -> printBlock2Colomns(i.blockA)
                PrintingMode.SERIES -> printBlock(i.blockA)
            }
        } else {
            when (diffMode) {
                PrintingMode.SPLIT -> printBlock2Colomns(i.blockA, i.blockB, Pair(SignType.DELETED, SignType.ADDED))
                PrintingMode.SERIES -> {printBlock(i.blockA, SignType.DELETED, Color.RED);
                                        printBlock(i.blockB, SignType.ADDED, Color.GREEN)}
            }
        }
    }
}

enum class Place {
    FIRST, LAST
}

fun printWithBorder(core: CompareCore, border: Int) {

    fun printTopOrBottomCommonBlock(block: CompareCore.TextBlock, position: Place) {
        if (block.size > border) {
            when (position) {
                Place.FIRST -> {
                    printSeparator();
                    printBlock2Colomns(block.text.sliceArray((block.size - border) until block.size))
                }
                Place.LAST -> {
                    printBlock2Colomns(block.text.sliceArray(0 until border))
                    printSeparator();
                }
            }
        } else {
            printBlock2Colomns(block)
        }
    }

    fun printCommonBlock(block: CompareCore.TextBlock) {
        if (block.size > border * 2) {
            printBlock2Colomns(block.text.sliceArray(0 until border))
            printSeparator();
            printBlock2Colomns(block.text.sliceArray(block.size - border until block.size))
        } else {
            printBlock2Colomns(block)
        }
    }

    for (i in core.diff.indices) {
        val it = core.diff[i]
        if (it.blockA === it.blockB) {
            when (i) {
                0 -> printTopOrBottomCommonBlock(it.blockA, Place.FIRST)
                core.diff.size - 1 -> printTopOrBottomCommonBlock(it.blockA, Place.LAST)
                else -> printCommonBlock(it.blockA)
            }
        } else {
            printBlock2Colomns(it.blockA, it.blockB, Pair(SignType.DELETED, SignType.ADDED))
        }
    }
}