package com.example.aboutme

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.aboutme.data.Address
import com.example.aboutme.data.Me
import com.example.aboutme.util.dateToLong
import com.example.aboutme.util.dateToString
import com.example.aboutme.util.save
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.part_address_info.*
import kotlinx.android.synthetic.main.part_interest.*
import kotlinx.android.synthetic.main.part_job_and_study.*
import kotlinx.android.synthetic.main.part_main_info.*
import kotlinx.android.synthetic.main.part_social_settings.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.title_settings)

        fillFields()

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
            DatePickerFragment().show(supportFragmentManager, getString(R.string.choose_a_birthday))
        }

        b_save.setOnClickListener {
            me = fillMe()
            save(this, store_file, me)
            finish()
        }
    }

    private fun fillMe() =
        Me (
            name = et_name.text.toString(),
            surname = et_surname.text.toString(),
            sex = if(is_a_man.isChecked) 0 else 1,
            birthday = dateToLong(tw_birthday.text.toString()),
            address = Address(
                country = et_country.text.toString(),
                city = et_city.text.toString(),
                address = et_address.text.toString()
            ),
            works = et_work.text.toString(),
            education = et_study.text.toString(),
            hobbies = et_hobbies.text.toString(),
            lovingMovies = et_loving_movies.text.toString(),
            lovingMusic = et_loving_music.text.toString(),
            lovingBooks = et_loving_books.text.toString(),
            phoneNumber = et_phonenumber.text.toString(),
            email = et_email.text.toString(),
            social = mapOf(
                "facebook" to et_facebook.text.toString(),
                "google" to et_google.text.toString(),
                "instagram" to et_instagram.text.toString(),
                "whatsapp" to et_whatsup.text.toString(),
                "twitter" to et_twitter.text.toString(),
                "youtube" to et_youtube.text.toString()
            )
        )

    private fun fillFields() =
        with(me){
            if(sex == 0) is_a_man.isChecked = true
            else is_a_woman.isChecked = true
            et_name.setText(name)
            et_surname.setText(surname)
            tw_birthday.text = dateToString(birthday)
            et_country.setText(address.country)
            et_city.setText(address.city)
            et_address.setText(address.address)
            et_work.setText(works)
            et_study.setText(education)
            et_hobbies.setText(hobbies)
            et_loving_movies.setText(lovingMovies)
            et_loving_music.setText(lovingMusic)
            et_loving_books.setText(lovingBooks)
            et_phonenumber.setText(phoneNumber)
            et_email.setText(email)
            et_facebook.setText(social.get("facebook"))
            et_google.setText(social.get("google"))
            et_instagram.setText(social.get("instagram"))
            et_whatsup.setText(social.get("whatsapp"))
            et_twitter.setText(social.get("twitter"))
            et_youtube.setText(social.get("youtube"))
        }
}
