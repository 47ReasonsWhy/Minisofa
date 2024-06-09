package com.sofascoreacademy.minisofa.ui.util

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.data.repository.Resource

fun Context.getColorFromAttr(@AttrRes attr: Int): Int {
    return MaterialColors.getColor(
        this,
        attr,
        getColor(R.color.transparent)
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

fun <T> Resource<List<T>>.processAsListForRecyclerView(
    recyclerView: RecyclerView,
    noItems: TextView,
    noConnection: TextView,
    loading: ProgressBar,
    submitList: (List<T>) -> Unit
) {
    when (this) {
        is Resource.Failure -> {
            loading.visibility = View.GONE
            recyclerView.visibility = View.GONE
            noItems.visibility = View.GONE
            noConnection.visibility = View.VISIBLE
        }
        is Resource.Loading -> {
            recyclerView.visibility = View.GONE
            noItems.visibility = View.GONE
            noConnection.visibility = View.GONE
            if (this.data?.isNotEmpty() == true) {
                submitList(this.data)
                recyclerView.visibility = View.VISIBLE
            }
            loading.visibility = View.VISIBLE
        }
        is Resource.Success -> {
            loading.visibility = View.GONE
            noConnection.visibility = View.GONE
            if (this.data.isEmpty()) {
                recyclerView.visibility = View.GONE
                noItems.visibility = View.VISIBLE
            } else {
                noItems.visibility = View.GONE
                submitList(this.data)
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}
