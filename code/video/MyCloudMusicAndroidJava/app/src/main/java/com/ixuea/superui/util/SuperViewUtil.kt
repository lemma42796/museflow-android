package com.ixuea.superui.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.ViewGroup

object SuperViewUtil {
    @JvmStatic
    fun show(data: View) {
        data.visibility = View.VISIBLE
    }

    @JvmStatic
    fun gone(data: View) {
        data.visibility = View.GONE
    }

    @JvmStatic
    fun gone(view: View, gone: Boolean) {
        view.visibility = if (gone) View.GONE else View.VISIBLE
    }

    @JvmStatic
    fun show(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    fun removeFromParent(data: View) {
        (data.parent as ViewGroup).removeView(data)
    }

    @JvmStatic
    fun captureBitmap(data: View): Bitmap {
        val bitmap = Bitmap.createBitmap(data.width, data.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val background = data.background
        if (background != null) {
            background.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        data.draw(canvas)
        return bitmap
    }

    @JvmStatic
    fun resize(view: View, width: Int, height: Int) {
        val layoutParams = view.layoutParams
        layoutParams.width = width
        layoutParams.height = height
    }
}
