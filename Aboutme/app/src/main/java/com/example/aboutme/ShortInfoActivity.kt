package com.example.aboutme

import android.os.Bundle

class ShortInfoActivity : MainActivity(){
    override fun createFragment() = ShortInfoFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
    }
}