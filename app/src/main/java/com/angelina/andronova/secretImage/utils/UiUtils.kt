package com.angelina.andronova.secretImage.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity?.hideKeyboard() {
    this ?: return
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) view = View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}