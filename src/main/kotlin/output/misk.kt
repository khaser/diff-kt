package output

import java.lang.Integer.max

enum class Color {
    BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, GRAY, WHITE
}

enum class Sign {
    DELETED, ADDED, NONE
}

enum class PrintLineMode {
    NEWLINE, INLINE
}

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



fun printBlock2Colomns(block: CompareCore.TextBlock, color: Color = Color.WHITE) {
    printBlock2Colomns(block, block, Pair(Sign.NONE, Sign.NONE), Pair(color, color));
}

fun printBlock2Colomns(blockA: CompareCore.TextBlock, blockB: CompareCore.TextBlock,
                       sign: Pair<Sign, Sign> = Pair(Sign.NONE, Sign.NONE), color: Pair<Color, Color> = Pair(Color.RED, Color.GREEN)) {
    printLine2Colomns(getSign(blockA.seg, sign.first), getSign(blockB.seg, sign.second), Pair(Color.PURPLE, Color.PURPLE))
    printBlock2Colomns(blockA.text, blockB.text, color);
}

fun printBlock2Colomns (block: Array<String>, color: Color = Color.WHITE) {
    printBlock2Colomns(block, block, Pair(color, color));
}

fun printBlock2Colomns (blockA: Array<String>, blockB: Array<String> = blockA,
                        color: Pair<Color, Color> = Pair(Color.RED, Color.GREEN)) {
    repeat(max(blockA.size, blockB.size)) {
        printLine2Colomns(
            if (it < blockA.size) blockA[it] else "",
            if (it < blockB.size) blockB[it] else "",
            color
        )
    }
}


fun printLine2Colomns(strA: String, strB: String, color: Pair<Color, Color> = Pair(Color.RED, Color.GREEN)) {
    printLine(strA.padEnd(colWidth), color.first, PrintLineMode.INLINE)
    printLine("||", Color.YELLOW, PrintLineMode.INLINE)
    printLine(strB, color.second)
}

fun printBlock(block: CompareCore.TextBlock, sign: Sign = Sign.NONE, color: Color = Color.WHITE) {
    printLine(getSign(block.seg, sign), Color.PURPLE)
    printBlock(block.text, color)
}

fun printBlock(str: Array<String>, color: Color = Color.WHITE) {
    str.forEach { printLine(it, color) }
}

fun printSeparator(color: Color = Color.BLUE) {
    printLine("".padEnd(colWidth * 2, '-'), color)
}

fun getSign(seg: CompareCore.Segment, sign: Sign): String {
    return when (sign) {
        Sign.ADDED -> if (longSign) "Added strings from ${seg.from + 1} to ${seg.to + 1}"
                      else "+${seg.from + 1}-${seg.to + 1}"
        Sign.DELETED -> if (longSign) "Deleted strings from ${seg.from + 1} to ${seg.to + 1}"
                       else "-${seg.from + 1}-${seg.to + 1}"
        Sign.NONE -> ""
    }
}

fun printLine(str: String, color: Color = Color.WHITE, mode: PrintLineMode = PrintLineMode.NEWLINE) {
    setColor(color)
    when (mode) {
        PrintLineMode.INLINE -> print(str)
        PrintLineMode.NEWLINE -> println(str)
    }
    setColor(Color.WHITE)
}