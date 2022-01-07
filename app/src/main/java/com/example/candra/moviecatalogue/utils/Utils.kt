package com.example.candra.moviecatalogue.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.example.candra.moviecatalogue.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


/*
 * Connection related functions
 */
fun isConnectedToInternet(context: Context?): Boolean {
    val connManager: ConnectivityManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}


/*
 * String related functions
 */
fun List<String>.printAll(): String {
    var printed = ""
    val size = this.size
    when {
        size == 0 -> printed = "null"
        size == 1 -> printed = "${this[0]} m"

        size > 1 -> {
            for (element in 0 until this.size - 1) {
                printed += "$element m, "
            }
            printed += "${this[this.size - 1]} m"
        }
    }
    return printed
}

fun String?.printNoticeIfNull(context: Context): String {
    return if (this == null || this == "") {
        context.getString(R.string.content_not_available)
    } else this
}

fun String?.formatToHourMinutes(context: Context): String {
    var formated = "null"
    this?.let {
        val number = it.toInt()
        formated = if (number <= 60) {
            String.format(context.resources.getString(R.string.runtimeMinutesOnly), number)
        } else {
            val hour = number / 60
            val minutes = number % 60
            String.format(context.resources.getString(R.string.runtimeField), hour, minutes)
        }
    }
    return formated
}

@SuppressLint("SimpleDateFormat")
fun String?.formatToLocalDate(
    locale: Locale,
    currentFormat: String = "yyyy-MM-dd",
    newFormat: String = "dd MMM yyyy"
): String {
    val date = SimpleDateFormat(currentFormat).parse(this)
    return SimpleDateFormat(newFormat, locale).format(date)
}

fun String?.formatToCurrency(locale: Locale): String {
    var formatted = ""
    this?.let {
        val number = it.toInt()
        formatted = NumberFormat.getCurrencyInstance(locale).format(number)
    }
    return formatted
}