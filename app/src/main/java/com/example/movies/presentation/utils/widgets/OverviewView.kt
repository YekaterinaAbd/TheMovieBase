package com.example.movies.presentation.utils.widgets

import android.animation.ObjectAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.movies.R

class OverviewView : LinearLayout {

    private lateinit var overview: TextView
    private lateinit var tvExpandCollapse: TextView
    private var isExpanded = false
    private val maxLineCount = 5

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    fun setOverviewText(text: String?) {
        overview.text = text
        setAppropriateText(overview)
    }

    private fun setAppropriateText(textView: TextView) {

        textView.post {
            val lineCount = textView.lineCount

            if (lineCount - maxLineCount > 2) {
                textView.maxLines = maxLineCount
                textView.ellipsize = TextUtils.TruncateAt.END
                tvExpandCollapse.visibility = View.VISIBLE

                tvExpandCollapse.setOnClickListener {
                    if (!isExpanded) {
                        tvExpandCollapse.text = context.getString(R.string.collapse)
                        expandCollapse(textView, maxLineCount, lineCount)
                        textView.ellipsize = null
                        isExpanded = true

                    } else {
                        tvExpandCollapse.text = context.getString(R.string.expand)
                        expandCollapse(textView, lineCount, maxLineCount)
                        textView.ellipsize = TextUtils.TruncateAt.END
                        isExpanded = false
                    }
                }

            } else {
                tvExpandCollapse.visibility = View.GONE
            }
        }
    }

    private fun expandCollapse(textView: TextView, startLine: Int, endLine: Int) {
        val animation =
            ObjectAnimator.ofInt(textView, "maxLines", startLine, endLine)
        animation.setDuration(200).start()
    }

    private fun init(context: Context) {
        inflate(context, R.layout.view_overview, this)

        overview = findViewById(R.id.description)
        tvExpandCollapse = findViewById(R.id.tvExpandCollapse)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        init(context)
    }
}