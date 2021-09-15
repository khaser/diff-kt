package output

import java.lang.Integer.max

//Enum for tuning output of function printAll
enum class PrintingMode {
    SPLIT, SERIES, NONE
}

//Enums for tuning sign of all diff blocks
enum class SignPrintingMode {
    LONG, SHORT, NONE
}

enum class SignType {
    DELETED, ADDED, NONE
}

enum class Color {
    BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, GRAY, WHITE
}

//Enum for function printLine
enum class PrintLineMode {
    NEWLINE, INLINE
}

//Works only for console output!!!
fun setColor(color: Color) {
    print(
        when (color) {
            Color.BLACK -> "\u001B[30m"
            Color.RED -> "\u001B[31m"
            Color.GREEN -> "\u001B[32m"
            Color.YELLOW -> "\u001B[33m"
            Color.BLUE -> "\u001B[34m"
            Color.PURPLE -> "\u001B[35m"
            Color.GRAY -> "\u001B[37m"
            Color.WHITE -> "\u001B[0m"
        }
    )
}


//Some functions for implementation SPLIT PrintingMode
fun printBlock2Columns(block: CompareCore.TextBlock, color: Color = Color.WHITE) {
    printBlock2Columns(block, block, Pair(SignType.NONE, SignType.NONE), Pair(color, color));
}

fun printBlock2Columns(blockA: CompareCore.TextBlock, blockB: CompareCore.TextBlock,
                       sign: Pair<SignType, SignType> = Pair(SignType.NONE, SignType.NONE), color: Pair<Color, Color> = Pair(Color.RED, Color.GREEN)) {
    if (sign.first != SignType.NONE && signMode != SignPrintingMode.NONE)
        printLine2Columns(getSign(blockA.seg, sign.first), getSign(blockB.seg, sign.second), Pair(Color.PURPLE, Color.PURPLE))
    printBlock2Columns(blockA.text, blockB.text, color);
}

fun printBlock2Columns (block: Array<String>, color: Color = Color.WHITE) {
    printBlock2Columns(block, block, Pair(color, color));
}

fun printBlock2Columns (blockA: Array<String>, blockB: Array<String> = blockA,
                        color: Pair<Color, Color> = Pair(Color.RED, Color.GREEN)) {
    repeat(max(blockA.size, blockB.size)) {
        printLine2Columns(
            if (it < blockA.size) blockA[it] else "",
            if (it < blockB.size) blockB[it] else "",
            color
        )
    }
}


fun printLine2Columns(strA: String, strB: String, color: Pair<Color, Color> = Pair(Color.RED, Color.GREEN)) {
    val leftStr = if (strA.length <= colWidth) strA else strA.dropLast(max(0, strA.length - colWidth + 3)) + "..."
    val rightStr = if (strB.length <= colWidth) strB else strB.dropLast(max(0, strB.length - colWidth + 3)) + "..."
    printLine(leftStr.padEnd(colWidth), color.first, PrintLineMode.INLINE)
    printLine("||", Color.YELLOW, PrintLineMode.INLINE)
    printLine(rightStr, color.second)
}

//Some functions for implementation SERIES PrintingMode
fun printBlock(block: CompareCore.TextBlock, sign: SignType = SignType.NONE, color: Color = Color.WHITE) {
    val generatedSign = getSign(block.seg, sign)
    if (generatedSign != "") printLine(getSign(block.seg, sign), Color.PURPLE)
    printBlock(block.text, color)
}

fun printBlock(str: Array<String>, color: Color = Color.WHITE) {
    str.forEach { printLine(it, color) }
}

//Print separator for context mode
fun printSeparator(color: Color = Color.BLUE) {
    printLine("".padEnd(colWidth * 2, '-'), color)
}

//Print sign for each diff block.
//To evade black lines in output after calling you should check if sign is not "" then print
fun getSign(seg: CompareCore.Segment, sign: SignType): String {
    return when (sign) {
        SignType.ADDED -> when (signMode) {
            SignPrintingMode.LONG -> "Added strings from ${seg.from + 1} to ${seg.to + 1}"
            SignPrintingMode.SHORT -> "A ${seg.from + 1}-${seg.to + 1}"
            SignPrintingMode.NONE -> ""
        }
        SignType.DELETED -> when (signMode) {
            SignPrintingMode.LONG -> "Deleted strings from ${seg.from + 1} to ${seg.to + 1}"
            SignPrintingMode.SHORT -> "D ${seg.from + 1}-${seg.to + 1}"
            SignPrintingMode.NONE -> ""
        }
        SignType.NONE -> ""
    }
}

//Redefinition default function for colored output
fun printLine(str: String, color: Color = Color.WHITE, mode: PrintLineMode = PrintLineMode.NEWLINE) {
    setColor(color)
    if (outputFile != null) {
        when (mode) {
            PrintLineMode.INLINE -> outputFile?.appendText(str)
            PrintLineMode.NEWLINE -> outputFile?.appendText(str + "\n")
        }
    } else {
        when (mode) {
            PrintLineMode.INLINE -> print(str)
            PrintLineMode.NEWLINE -> println(str)
        }
    }
    setColor(Color.WHITE)
}
