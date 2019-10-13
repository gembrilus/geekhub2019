package com.example.aboutme.util

import android.content.Context
import android.widget.Toast
import com.example.aboutme.R
import com.example.aboutme.data.Me
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

fun load(context: Context, file: File): Me {
    var input: ObjectInputStream? = null
    var me = Me()
    try {
        input = ObjectInputStream(FileInputStream(file))
        me = input.readObject() as Me
    } catch (e: IOException) {
        showErrorPopup(context, context.getString(R.string.can_not_load_data))
    } finally {
        input?.close()
    }
    return me
}

fun save(context: Context, file: File, obj: Me) {
    var output: ObjectOutputStream? = null
    try {
        output = ObjectOutputStream(FileOutputStream(file))
        output.writeObject(obj)
    } catch (e: IOException) {
        showErrorPopup(context, context.getString(R.string.can_not_save_data))
    } finally {
        output?.close()
    }
}

fun showErrorPopup(context: Context, s: String) = Toast.makeText(context, s, Toast.LENGTH_SHORT).show()

fun birthday(otherDate: String?): Int? {
    var a: Int? = null
    if (otherDate == null) return null
    val df = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
    val date = df.parse(otherDate)
    val cal = Calendar.getInstance()
    cal.time = date ?: return null

    a = Calendar.getInstance().get(Calendar.YEAR) - cal.get(Calendar.YEAR)
    if (Calendar.getInstance().get(Calendar.MONTH) < cal.get(Calendar.MONTH) ||
        (Calendar.getInstance().get(Calendar.MONTH) < cal.get(Calendar.MONTH) &&
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH))
    ) {
        --a
    }
    return a
}

fun getInvitation(context: Context, obj: Me): String {
    val bDay = birthday(obj.birthday) ?: 0
    val s = when(bDay % 10){
        1 -> context.getString(R.string.year)
        in 2..4 -> context.getString(R.string.years)
        else -> context.getString(R.string.years2)
    }
    return "${obj.name} ${obj.surname}\n${bDay} $s"
}