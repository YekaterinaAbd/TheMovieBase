package com.example.movies.core.view

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.movies.R

class LoaderDialog(
    context: Context
) {
    private var dialog: Dialog = Dialog(context, R.style.Dialog_Transparent)

    private val view by lazy {
        LayoutInflater.from(context).inflate(R.layout.dialog_loader, null)
    }

    init {
        dialog.setContentView(view)
    }

    fun show() {
        dialog.show()
    }

    fun hide() {
        dialog.dismiss()
    }

    fun showOrHide(show: Boolean) {
        when (show) {
            true -> show()
            false -> hide()
        }
    }
}