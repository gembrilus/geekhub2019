package com.example.aboutme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aboutme.data.Me
import kotlinx.android.synthetic.main.activity_additional_info.*

class AdditionalInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)

        tw_info_about.text = (intent.getSerializableExtra("ME") as Me).toString()
    }

}
