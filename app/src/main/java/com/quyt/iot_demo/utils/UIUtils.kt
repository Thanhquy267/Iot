package com.quyt.iot_demo.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class UIUtils {

    companion object {
        fun hideKeyboard(context: Context, view: View?) {
            if (view != null) {
                val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        fun showKeyboard(context: Context?, view: View?) {
            if (view != null) {
                val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }
    }
}