package com.example.aboutme

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aboutme.data.Me
import kotlinx.android.synthetic.main.activity_invite.*
import java.io.File

class InviteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)
        setTheme(android.R.style.Animation_Dialog)

        b_ok.setOnClickListener {
            val name = name.text.toString()
            val surname = surname.text.toString()
            if (name != "" || surname != "") {
                val file = File(filesDir, "me_store")
                MainActivity.save(file, Me(name, surname))
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                }
            else {
                    Toast.makeText(
                        this,
                        "Введите, пожалуйста, ваши имя и фамилию",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        b_cancel.setOnClickListener {
            finish()
        }
    }
}
