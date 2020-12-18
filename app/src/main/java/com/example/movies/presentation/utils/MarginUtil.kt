package com.example.movies.presentation.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

enum class Side {
    TOP,
    BOTTOM,
    RIGHT,
    LEFT
}

object Margin {

    fun setMargin(sizeInDP: Int, context: Context, view: View, side: Side) {

        val marginInDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeInDP.toFloat(), context.resources
                .displayMetrics
        ).toInt()

        val params: ViewGroup.MarginLayoutParams =
            view.layoutParams as ViewGroup.MarginLayoutParams

        when (side) {
            Side.TOP -> params.topMargin = marginInDp
            Side.BOTTOM -> params.bottomMargin = marginInDp
            Side.LEFT -> params.leftMargin = marginInDp
            Side.RIGHT -> params.rightMargin = marginInDp
        }
    }
}