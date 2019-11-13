package iv.nakonechnyi.newsfeeder.util

import android.app.AlertDialog
import android.content.Context
import iv.nakonechnyi.newsfeeder.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun stringToLongDate(date: String) =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(date)?.time

fun longDateToString(date: Long) =
    SimpleDateFormat("dd MMMM yyyy - HH:mm:ss", Locale.getDefault()).format(Date(date)).toString()

fun isLeapYear(year: Int) = when {
    year%400 == 0 -> true
    year%100 == 0 -> false
    year%4 == 0 -> true
    else -> false
}

val currentYear = Calendar.getInstance().get(Calendar.YEAR)
fun daysOfMonth(year: Int = currentYear) = when (Calendar.getInstance().get(Calendar.MONTH)) {
    Calendar.FEBRUARY   -> if(isLeapYear(year)) 29 else 28
    Calendar.APRIL,
    Calendar.JUNE,
    Calendar.SEPTEMBER,
    Calendar.NOVEMBER   -> 30
    else                -> 31
}

fun monthNameByNumber(id: Int): String {
    val locale = Locale.getDefault()
    val calendar = Calendar.getInstance(locale).apply { set(Calendar.MONTH, id) }
    val sdf = SimpleDateFormat("MMMM", locale)

    return sdf.format(calendar.time)

}

fun convertToDegrees(v: Double): String{
    val degrees = v.toInt()
    val min = ((v - degrees) * 60).toInt()
    val sec = ((v - degrees) * 60 - min) * 60
    return String.format("%d° %d′ %.2f″", abs(degrees), abs(min), abs(sec))
}

fun displayErrorMessage(context: Context, message: String): Unit {
    AlertDialog.Builder(context).apply {
        setMessage(message)
        setIcon(R.drawable.error_icon)
        setTitle("Error message!")
        setPositiveButton(android.R.string.ok, null)
        create()
    }.show()
}