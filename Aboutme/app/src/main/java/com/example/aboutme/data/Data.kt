package com.example.aboutme.data

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Me(val name: String = "Шаман", val surname: String = "Повалянбатыргора"): Serializable {
    var photos: String? = null
    var sex = "Мужской"
    var birthday = "7 февраля 1983"
    var age: Int? = null
        get() = GregorianCalendar.getInstance() - birthday
        set(value) { field = value }
    var address = Address()
    var works = "IT-плиточник"
    var education = "инженер-ломастер из Гарварда"
    var hobbies = "Поедание пончиков"
    var lovingMovies = "Звездные врата"
    var lovingMusic = "под настроение"
    var lovingBooks = "учебники"
    var phoneNumber = "+102 103 104"
    var email = "tuneyadec@uh.ty"
    var social = mapOf("facebook" to "http://zukerberg")
}

data class Address(val country: String = "Китай",
                   val City: String = "Тянь-Ши",
                   val address: String = "Загогулько 10, кв. 5") : Serializable

private operator fun Calendar.minus(otherDate: String?): Int? {
    var a: Int? = null
    if (otherDate == null) return null
    val df = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    val date = df.parse(otherDate)
    val cal = Calendar.getInstance()
    cal.time = date ?: return null

    a = get(Calendar.YEAR) - cal.get(Calendar.YEAR)
    if (get(Calendar.MONTH) < cal.get(Calendar.MONTH) ||
        (get(Calendar.MONTH) < cal.get(Calendar.MONTH) &&
                get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH))
    ) {
        --a
    }
    return a
}
