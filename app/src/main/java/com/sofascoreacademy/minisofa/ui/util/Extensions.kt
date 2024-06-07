package com.sofascoreacademy.minisofa.ui.util

import android.content.Context
import android.widget.TextView
import androidx.annotation.AttrRes
import com.google.android.material.color.MaterialColors
import com.sofascoreacademy.minisofa.R

fun getColorFromAttr(@AttrRes attr: Int, context: Context): Int {
    return MaterialColors.getColor(
        context,
        attr,
        context.getColor(R.color.transparent)
    )
}

fun TextView.setTextColorFromAttr(@AttrRes attr: Int) {
    setTextColor(
        MaterialColors.getColor(
        context,
        attr,
        context.getColor(R.color.transparent)
    ))
}
