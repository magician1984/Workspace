package com.auo.dvr_ui.presentation.contents

import android.graphics.Bitmap

object ImageUtils {
    fun uyvy422ToBitmap(uyvy: ByteArray, width: Int, height: Int): Bitmap {
        val pixels = IntArray(width * height)

        var i = 0 // index in uyvy[]
        var p = 0 // index in pixels[]

        while (i < uyvy.size && p + 1 < pixels.size) {
            val u = (uyvy[i].toInt() and 0xFF) - 128
            val y0 = (uyvy[i + 1].toInt() and 0xFF)
            val v = (uyvy[i + 2].toInt() and 0xFF) - 128
            val y1 = (uyvy[i + 3].toInt() and 0xFF)

            pixels[p] = yuvToRgb(y0, u, v)
            pixels[p + 1] = yuvToRgb(y1, u, v)

            i += 4
            p += 2
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun yuvToRgb(y: Int, u: Int, v: Int): Int {
        var r = (y + 1.402 * v).toInt()
        var g = (y - 0.344136 * u - 0.714136 * v).toInt()
        var b = (y + 1.772 * u).toInt()

        r = r.coerceIn(0, 255)
        g = g.coerceIn(0, 255)
        b = b.coerceIn(0, 255)

        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
}