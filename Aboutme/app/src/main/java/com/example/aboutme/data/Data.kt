package com.example.aboutme.data

import java.io.Serializable

data class Me(
    var name: String = "Шаман",
    var surname: String = "Повалянбатыргора",
    var photos: String = "",
    var sex: Int = -1,
    var birthday: String = "07 02 1983",
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
