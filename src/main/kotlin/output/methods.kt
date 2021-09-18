package output

import CompareCore
import input.Option as Options
import input.keyMathing
import java.io.File

//Package for output object CompareCore.diff in different modes, determined by user.
//This file contains semantic part of package, all auxiliary functions can be find in misk.kt

var colWidth = 80
var outputFile: File? = null
var signMode = SignPrintingMode.LONG
var diffMode = PrintingMode.SPLIT
var commonMode = PrintingMode.SPLIT

//Main function of this package and entrypoint
fun printDiff(core: CompareCore, options: Map<Options, String>) {

    val minWidth = core.diff.maxOf { it.blockA.width }

    colWidth = options[Options.WIDTH]?.toIntOrNull() ?: minWidth

    if (options.containsKey(Options.FILE)) {
        outputFile = File(options[Options.FILE]!!)
    }

    //Init signMode, diffMode, commonMode from input arguments
    keyMathing(options)

    if (options[Options.ENABLE_CONTEXT] == "true") {
        printWithBorder(core, options[Options.CONTEXT_BORDER]?.toIntOrNull() ?: 5)
    } else {
        printAll(core)
    }
}

//Implementation of default diff, customized by commonMode and diffMode
private fun printAll(core: CompareCore) {
    for (i in core.diff) {
        if (i.blockA === i.blockB) {
            when (commonMode) {
                PrintingMode.SPLIT -> printBlock2Columns(i.blockA)
                PrintingMode.SERIES -> printBlock(i.blockA)
            }
        } else {
            when (diffMode) {
                PrintingMode.SPLIT -> printBlock2Columns(i.blockA, i.blockB, Pair(SignType.DELETED, SignType.ADDED))
                PrintingMode.SERIES -> {
                    printBlock(i.blockA, SignType.DELETED, Color.RED);
                    printBlock(i.blockB, SignType.ADDED, Color.GREEN)
                }
            }
        }
    }
}

//Enum for definition place of incomplete common block. Used only by printWithBorder
enum class Place {
    FIRST, LAST
}

//Implementation of context diff, customized only by border
private fun printWithBorder(core: CompareCore, border: Int) {

    fun printTopOrBottomCommonBlock(block: CompareCore.TextBlock, position: Place) {
        if (block.size > border) {
            when (position) {
                Place.FIRST -> {
                    printSeparator();
                    printBlock2Columns(block.text.slice((block.size - border) until block.size))
                }
                Place.LAST -> {
                    printBlock2Columns(block.text.slice(0 until border))
                    printSeparator();
                }
            }
        } else {
            printBlock2Columns(block)
        }
    }

    fun printCommonBlock(block: CompareCore.TextBlock) {
        if (block.size > border * 2) {
            printBlock2Columns(block.text.slice(0 until border))
            printSeparator();
            printBlock2Columns(block.text.slice(block.size - border until block.size))
        } else {
            printBlock2Columns(block)
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
            printBlock2Columns(it.blockA, it.blockB, Pair(SignType.DELETED, SignType.ADDED))
        }
    }
}