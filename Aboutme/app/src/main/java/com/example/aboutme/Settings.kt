package com.example.aboutme

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.part_address_info.*
import kotlinx.android.synthetic.main.part_interest.*
import kotlinx.android.synthetic.main.part_job_and_study.*
import kotlinx.android.synthetic.main.part_main_info.*
import kotlinx.android.synthetic.main.part_photo_picker.*
import kotlinx.android.synthetic.main.part_social_settings.*

class Settings : FragmentActivity() {

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

        hided_group6.setOnClickListener {
            if (ll_group6.isVisible) ll_group6.visibility = View.GONE
            else ll_group6.visibility = View.VISIBLE
        }

        ib_date.setOnClickListener {
            DatePickerFragment().show(supportFragmentManager, "Выбор даты рождения")
        }


    }
}
