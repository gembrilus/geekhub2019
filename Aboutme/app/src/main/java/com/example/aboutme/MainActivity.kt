package com.example.aboutme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aboutme.data.Me
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    private lateinit var me: Me

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        iv_photo.apply {
            if (me.photos.isNullOrEmpty()) {
                setImageDrawable(resources.getDrawable(R.drawable.anonim_photo))
            }
            else {
                setImageURI(Uri.parse(me.photos))
            }
        }

        b_info_text.setOnClickListener {
            val intent = Intent(this, AdditionalInfo::class.java)
            intent.putExtra("ME", me)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        tw_invitation.text = "${me.name} ${me.surname}"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun initialize() {
        val file = File(filesDir, "me_store")
        if (file.exists()) {
            me = load(this, file)
        } else me = Me()
    }

    fun setInfo(item: MenuItem) {
        val intent = Intent(this, Settings::class.java)
        intent.putExtra("ME", me)
        startActivity(intent)
    }
}

fun load(context: Context, file: File): Me {
    var input: ObjectInputStream? = null
    var me = Me()
    try {
        input = ObjectInputStream(FileInputStream(file))
        me = input.readObject() as Me
    } catch (e: IOException) {
        showErrorPopup(context, "Не удалось загрузить данные")
    } finally {
        input?.close()
    }
    return me
}

fun save(context: Context, file: File, obj: Me) {
    var output: ObjectOutputStream? = null
    try {
        output = ObjectOutputStream(FileOutputStream(file))
        output.writeObject(obj)
    } catch (e: IOException) {
        showErrorPopup(context, "Не удалось сохранить данные")
    } finally {
        output?.close()
    }
}

fun showErrorPopup(context: Context, s: String) = Toast.makeText(context, s, Toast.LENGTH_SHORT).show()