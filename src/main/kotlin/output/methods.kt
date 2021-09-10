package output
import CompareCore

enum class PrintingMode {
    SPLIT, SERIES, NONE
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
                PrintingMode.SPLIT -> printBlock2Colomns(i.blockA, i.blockB)
                PrintingMode.SERIES -> {printBlock(i.blockA, Color.RED); printBlock(i.blockB, Color.GREEN)}
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
            printBlock2Colomns(it.blockA, it.blockB)
        }
    }
}