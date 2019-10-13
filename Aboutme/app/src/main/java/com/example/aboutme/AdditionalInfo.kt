package com.example.aboutme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aboutme.data.Me

class AdditionalInfo : AppCompatActivity() {

    private lateinit var me: Me

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)

        me = intent.getSerializableExtra("ME") as Me
    }

}
