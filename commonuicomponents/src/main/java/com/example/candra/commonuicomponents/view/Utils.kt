package com.example.candra.commonuicomponents.view

import android.content.Context
import com.example.candra.commonuicomponents.R

fun String?.printNoticeIfNull(context: Context): String {
    return if (this == null || this == "") {
        context.getString(R.string.content_not_available)
    } else this
}