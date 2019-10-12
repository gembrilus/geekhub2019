package com.example.aboutme

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class AdditionalInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu == null) return false
        menuInflater.inflate(R.menu.menu, menu)
        menu.setGroupVisible(R.id.group1, false)
        return true
    }

    fun setInfo(item: MenuItem){
        startActivity(Intent(this, Settings::class.java))
    }
    fun setTheme(item: MenuItem){}
    fun exit(item: MenuItem){}
}
