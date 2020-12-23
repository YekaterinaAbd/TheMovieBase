package com.example.movies.presentation.utils.extensions

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup

enum class Side { TOP, BOTTOM, START, END }

fun View.setMargin(dpMargin: Int, side: Side) {
    val params: ViewGroup.MarginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    when (side) {
        Side.TOP -> params.topMargin = dpMargin.px
        Side.BOTTOM -> params.bottomMargin = dpMargin.px
        Side.END -> params.leftMargin = dpMargin.px
        Side.START -> params.rightMargin = dpMargin.px
    }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
