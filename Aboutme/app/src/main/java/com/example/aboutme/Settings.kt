package com.example.aboutme

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.aboutme.data.Address
import com.example.aboutme.data.Me
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.part_address_info.*
import kotlinx.android.synthetic.main.part_interest.*
import kotlinx.android.synthetic.main.part_job_and_study.*
import kotlinx.android.synthetic.main.part_main_info.*
import kotlinx.android.synthetic.main.part_social_settings.*
import java.io.File


class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        hided_group1.setOnClickListener {
            if (ll_group1.isVisible) ll_group1.visibility = View.GONE
            else ll_group1.visibility = View.VISIBLE
        }

        hided_group2.setOnClickListener {
            if (ll_group2.isVisible) ll_group2.visibility = View.GONE
            else ll_group2.visibility = View.VISIBLE
        }

        hided_group3.setOnClickListener {
            if (ll_group3.isVisible) ll_group3.visibility = View.GONE
            else ll_group3.visibility = View.VISIBLE
        }

        hided_group4.setOnClickListener {
            if (ll_group4.isVisible) ll_group4.visibility = View.GONE
            else ll_group4.visibility = View.VISIBLE
        }

        hided_group5.setOnClickListener {
            if (ll_group5.isVisible) ll_group5.visibility = View.GONE
            else ll_group5.visibility = View.VISIBLE
        }

        ib_date.setOnClickListener {
            DatePickerFragment().show(supportFragmentManager, "Выбор даты рождения")
        }

        b_save.setOnClickListener {
            val me1 = fillMe()
            val file = File(filesDir, "me_store")
            save(this, file, me1)
        }
    }

    private fun fillMe(): Me {
        val me = intent.getSerializableExtra("ME") as Me
        me.sex = findViewById<RadioButton>(rg_sex.checkedRadioButtonId).text.toString()
        me.birthday = tw_birthday.text.toString()
        me.address = Address(et_country.text.toString(), et_city.text.toString(), et_address.text.toString())
        me.works = et_work.text.toString()
        me.education = et_study.text.toString()
        me.hobbies = et_hobbies.text.toString()
        me.lovingMovies = et_loving_movies.text.toString()
        me.lovingMusic = et_loving_music.text.toString()
        me.lovingBooks = et_loving_books.text.toString()
        me.phoneNumber = et_phonenumber.text.toString()
        me.email = et_email.text.toString()
        me.social = mapOf(
            "facebook" to et_facebook.text.toString(),
            "google" to et_google.text.toString(),
            "instagram" to et_instagram.text.toString(),
            "whatsapp" to et_whatsup.text.toString(),
            "twitter" to et_twitter.text.toString(),
            "youtube" to et_youtube.text.toString()
        )

        return me
    }

}
