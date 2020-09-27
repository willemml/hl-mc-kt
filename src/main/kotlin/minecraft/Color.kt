enum class Colors {
    black,
    dark_blue,
    dark_green,
    dark_aqua,
    dark_red,
    dark_purple,
    gold,
    gray,
    dark_gray,
    blue,
    green,
    aqua,
    red,
    light_purple,
    yellow,
    white
}

fun Int.toByteArray(): ByteArray {
    val arr = ByteArray(4)
    for (i in 0 until 4) {
        arr[i] = (this shr (24 - i * 8)).toByte()
    }
    return arr
}

class RGBColor(val red: Int = 0, val green: Int = 0, val blue: Int = 0) {
    fun toInt(): Int {
        return (((red.shl(8)) + green).shl(8)) + blue
    }
}

fun Colors.toRGB(): RGBColor {
    return when(this.ordinal) {
        1 -> RGBColor(0, 0, 170)
        2 -> RGBColor(0, 170, 170)
        3 -> RGBColor(0, 170, 170)
        4 -> RGBColor(170, 0, 0)
        5 -> RGBColor(170, 170, 0)
        6 -> RGBColor(255, 170, 0)
        7 -> RGBColor(170, 170, 170)
        8 -> RGBColor(85, 85, 85)
        9 -> RGBColor(85, 85, 255)
        10 -> RGBColor(85, 255, 85)
        11 -> RGBColor(85, 255, 255)
        12 -> RGBColor(255, 85, 85)
        13 -> RGBColor(255, 85, 255)
        14 -> RGBColor(255, 255, 85)
        15 -> RGBColor(255, 255, 255)
        else -> RGBColor(0, 0, 0)
    }
}

private const val legacyChar = "§"

fun Colors.getLegacy(): String {
    return legacyChar + when (this.ordinal) {
        10 -> "a"
        11 -> "b"
        12 -> "c"
        13 -> "d"
        14 -> "e"
        15 -> "f"
        else -> this.ordinal.toString()
    }
}

fun Colors.fromLegacy(input: String): Colors {
    for (color in Colors.values()) {
        if (color.getLegacy() == input) return color
    }
    return Colors.white
}