package iv.nakonechnyi.aboutme.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import android.widget.Toast
import iv.nakonechnyi.aboutme.R
import iv.nakonechnyi.aboutme.data.Me
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

fun load(file: File): Me {
    var input: ObjectInputStream? = null
    return try {
        input = ObjectInputStream(FileInputStream(file))
        input.readObject() as Me
    } catch (e: IOException) {
        Me()
    } finally {
        input?.close()
    }
}

fun save(context: Context, file: File, obj: Me) {
    var output: ObjectOutputStream? = null
    try {
        output = ObjectOutputStream(FileOutputStream(file))
        output.writeObject(obj)
    } catch (e: IOException) {
        showErrorPopup(
            context,
            context.getString(R.string.can_not_save_data)
        )
        e.printStackTrace()
    } finally {
        output?.close()
    }
}

fun showErrorPopup(context: Context, s: String) =
    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()

fun getAge(d: Long): Int {
    val date = Date(d)
    val cal = Calendar.getInstance()
    cal.time = date

    var a = Calendar.getInstance().get(Calendar.YEAR) - cal.get(Calendar.YEAR)
    if (Calendar.getInstance().get(Calendar.MONTH) < cal.get(Calendar.MONTH) ||
        (Calendar.getInstance().get(Calendar.MONTH) < cal.get(Calendar.MONTH) &&
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH))
    ) {
        --a
    }
    return a
}

fun dateToLong(d: String): Long = try {
    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(d).time
} catch (e: Throwable) {
    0L
}

fun dateToString(d: Long): String =
    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(d))


fun getDisplaySize(context: Context) = Point().also {
    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .defaultDisplay.getSize(it)
}