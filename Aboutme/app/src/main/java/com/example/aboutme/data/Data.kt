package com.example.aboutme.data

import android.app.Activity
import android.view.View
import kotlinx.android.synthetic.main.activity_additional_info.*
import java.io.Serializable

data class Me(
    var name: String = "Шаман",
    var surname: String = "Повалянбатыргора",
    var photos: String = "",
    var sex: Int = 0,
    var birthday: Long = 100000000000L,
    var address: Address = Address(),
    var works: String = "IT-плиточник",
    var education: String = "инженер-ломастер из Гарварда",
    var hobbies: String = "Поедание пончиков",
    var lovingMovies: String = "Звездные врата",
    var lovingMusic: String = "под настроение",
    var lovingBooks: String = "учебники",
    var phoneNumber: String = "+102 103 104",
    var email: String = "tuneyadec@uh.ty",
    var social: Map<String, String> = mapOf("facebook" to "http://zukerberg")
) : Serializable

data class Address(
    var country: String = "Китай",
    var city: String = "Тянь-Ши",
    var address: String = "Загогулько 10, кв. 5"
) : Serializable


fun Me.map(a: Activity): Map<View, String> {
    return mapOf(
        a.tw_address_info to "${address.country}, ${address.city}, ${address.address}",
        a.tw_work_info to works,
        a.tw_study_info to education,
        a.tw_hobbies_info to hobbies,
        a.tw_loving_movies_info to lovingMovies,
        a.tw_loving_music_info to lovingMusic,
        a.tw_loving_books_info to lovingBooks,
        a.tw_phonenumber_info to phoneNumber,
        a.tw_email_info to email
    )
}