package iv.nakonechnyi.newsfeeder

import android.app.AlertDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

fun stringToLongDate(date: String) = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(date)?.time
fun longDateToString(date: Long) = SimpleDateFormat("dd MMMM yyyy - HH:mm:ss", Locale.getDefault()).format(Date(date)).toString()
fun displayErrorMessage(context: Context, message: String): Unit {
    AlertDialog.Builder(context).apply {
        setMessage(message)
        setIcon(R.drawable.error_icon)
        setTitle("Error message!")
        setPositiveButton(android.R.string.ok, null)
        create()
    }.show()
}