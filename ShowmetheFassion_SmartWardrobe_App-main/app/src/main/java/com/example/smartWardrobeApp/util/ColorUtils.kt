package com.example.smartWardrobeApp.util

import android.graphics.BitmapFactory
import java.io.File


val colorMap = mapOf(
    "red" to Triple(255, 0, 0),
    "green" to Triple(0, 255, 0),
    "blue" to Triple(0, 0, 255),
    "black" to Triple(0, 0, 0),
    "white" to Triple(255, 255, 255),
    "gray" to Triple(128, 128, 128),
    "pink" to Triple(255, 192, 203),
    "yellow" to Triple(255, 255, 0),
    "orange" to Triple(255, 165, 0),
    "beige" to Triple(245, 245, 220),
    "denim" to Triple(36, 116, 208),
    "jean" to Triple(67, 107, 149),
    "purple" to Triple(128, 0, 128),
    "violet" to Triple(138, 43, 226),
    "indigo" to Triple(75, 0, 130),
    "brown" to Triple(139, 69, 19),
    "beige" to Triple(245, 245, 220),
    "khaki" to Triple(195, 176, 145),
    "gold" to Triple(255, 215, 0),
    "silver" to Triple(192, 192, 192),
    "navy" to Triple(0, 0, 128),
    "skyblue" to Triple(135, 206, 235),
    "turquoise" to Triple(64, 224, 208),
    "teal" to Triple(0, 128, 128),
    "lime" to Triple(0, 255, 128),
    "maroon" to Triple(128, 0, 0),
    "coral" to Triple(255, 127, 80),
    "salmon" to Triple(250, 128, 114),
    "lavender" to Triple(230, 230, 250),
    "mint" to Triple(189, 252, 201)
)

fun findClosestColorName(r: Int, g: Int, b: Int): String {
    return colorMap.minByOrNull { (_, rgb) ->
        val (cr, cg, cb) = rgb
        val dr = r - cr
        val dg = g - cg
        val db = b - cb
        dr * dr + dg * dg + db * db
    }?.key ?: "unknown"
}

fun getCenterAverageColor(imagePath: String): Triple<Int, Int, Int>? {
    val bitmap = BitmapFactory.decodeFile(File(imagePath).absolutePath) ?: return null
    val width = bitmap.width
    val height = bitmap.height
    if (width < 10 || height < 10) return null

    val startX = width / 2 - 5
    val startY = height / 2 - 5

    var sumR = 0
    var sumG = 0
    var sumB = 0
    var count = 0

    for (x in startX until startX + 10) {
        for (y in startY until startY + 10) {
            val pixel = bitmap.getPixel(x, y)
            sumR += (pixel shr 16) and 0xFF
            sumG += (pixel shr 8) and 0xFF
            sumB += pixel and 0xFF
            count++
        }
    }
    return Triple(sumR / count, sumG / count, sumB / count)
}

fun deriveColorNameFromImage(path: String): String? {
    val rgb = getCenterAverageColor(path) ?: return null
    return findClosestColorName(rgb.first, rgb.second, rgb.third)
}
