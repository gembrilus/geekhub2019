package com.example.aboutme.data

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Me(val name: String, val surname: String): Serializable {
    var photos: Array<String>? = null
    var sex: Int? = null
    var birthday: String? = null
    var age: Int? = null
        get() = GregorianCalendar.getInstance() - birthday
        set(value) { field = value }
    var address: Address? = null
    var works: String? = null
    var education: String? = null
    var hobbies: String? = null
    var lovingMovies: String? = null
    var lovingMusic: String? = null
    var lovingBooks: String? = null
    var phoneNumber: String? = null
    var email: String? = null
    var social: Map<String, String>? = null
}

data class Address(val country: String?, val City: String?, val address: String?) : Serializable

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
