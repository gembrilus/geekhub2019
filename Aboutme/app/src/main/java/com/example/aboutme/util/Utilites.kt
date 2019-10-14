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

fun birthday(d: Long): Int {
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

fun dateToLong(d: String): Long =
    try {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(d).time
    } catch (e: Exception){
        0L
    }

fun dateToString(d: Long): String {
    val date = Date(d)
    return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
}