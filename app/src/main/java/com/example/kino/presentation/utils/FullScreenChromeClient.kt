package com.example.kino.presentation.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

class FullScreenChromeClient(private val activity: Activity) : WebChromeClient() {

    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null
    private var originalOrientation: Int = 0
    private var fullscreenContainer: FrameLayout? = null

    @SuppressLint("ObsoleteSdkInt")
    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (customView != null) {
                callback.onCustomViewHidden()
                return
            }
            originalOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            activity.requestedOrientation = originalOrientation
            val decor = activity.window.decorView as FrameLayout
            fullscreenContainer = FullscreenHolder(activity)
            (fullscreenContainer as FullscreenHolder).setPadding(0, 0, 0, 30)
            fullscreenContainer?.addView(view, COVER_SCREEN_PARAMS)
            decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS)
            customView = view
            setFullscreen(true)
            customViewCallback = callback
        }

        super.onShowCustomView(view, callback)
    }

    override fun onShowCustomView(
        view: View,
        requestedOrientation: Int,
        callback: CustomViewCallback
    ) {
        this.onShowCustomView(view, callback)
    }

    override fun onHideCustomView() {
        if (customView == null) {
            return
        }

        setFullscreen(false)
        originalOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val decor = activity.window.decorView as FrameLayout
        decor.removeView(fullscreenContainer)
        fullscreenContainer = null
        customView = null
        customViewCallback?.onCustomViewHidden()
        activity.requestedOrientation = originalOrientation
    }

    private fun setFullscreen(enabled: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        val bits = WindowManager.LayoutParams.FLAG_FULLSCREEN
        if (enabled) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
            customView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        win.attributes = winParams
    }

    class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {
        init {
            setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black))
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }
    }

    companion object {

        val COVER_SCREEN_PARAMS = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}
