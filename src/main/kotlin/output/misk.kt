package output

import java.lang.Integer.max

enum class Color {
    BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, GRAY, WHITE
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



fun printBlock2Colomns(block: CompareCore.TextBlock, color: Color = Color.WHITE, colWidth: Int = 120) {
    printBlock2Colomns(block, block, color, color, colWidth);
}

fun printBlock2Colomns(blockA: CompareCore.TextBlock, blockB: CompareCore.TextBlock,
                       colorLeft: Color = Color.RED, colorRight: Color = Color.GREEN, colWidth: Int = 120) {
    printBlock2Colomns(blockA.text, blockB.text, colorLeft, colorRight, colWidth);
}

fun printBlock2Colomns (block: Array<String>, color: Color = Color.WHITE, colWidth: Int = 120) {
    printBlock2Colomns(block, block, color, color, colWidth);
}

fun printBlock2Colomns (blockA: Array<String>, blockB: Array<String> = blockA,
                        colorLeft: Color = Color.RED, colorRight: Color = Color.GREEN, colWidth: Int = 120) {
    repeat(max(blockA.size, blockB.size)) {
        printLine2Colomns(
            if (it < blockA.size) blockA[it] else "",
            if (it < blockB.size) blockB[it] else "",
            colorLeft, colorRight, colWidth
        )
    }
}


fun printLine2Colomns(strA: String, strB: String, colorLeft: Color = Color.RED,
                      colorRight: Color = Color.GREEN, colWidth: Int = 120) {
    setColor(colorLeft)
    print("${strA.padEnd(colWidth)}||")
    printLine(strB, colorRight)
}

fun printBlock(block: CompareCore.TextBlock, color: Color = Color.WHITE) {
    printBlock(block.text, color)
}

fun printBlock(str: Array<String>, color: Color = Color.WHITE) {
    str.forEach { printLine(it, color) }
}

fun printSeparator(color: Color = Color.BLUE, colWidth: Int = 240) {
    printLine("".padEnd(colWidth, '-'), color)
}


fun printLine(str: String, color: Color = Color.WHITE) {
    setColor(color)
    println(str)
    setColor(Color.WHITE)
}