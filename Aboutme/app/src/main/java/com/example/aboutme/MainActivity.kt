package com.example.aboutme

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
    internal var me: Me? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        iv_photo.apply {
            if (me?.photos.isNullOrEmpty()) {
                setImageDrawable(resources.getDrawable(R.drawable.anonim_photo))
            }
            else {
                setImageURI(Uri.parse(me?.photos))
            }
        }

        b_info_text.setOnClickListener {
            startActivity(Intent(this, AdditionalInfo::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        tw_invitation.text = "${tw_invitation.text} ${me?.name} ${me?.surname}"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    companion object {

        private val mainActivity = MainActivity()

        fun save(file: File, obj: Me) {
            var output: ObjectOutputStream? = null
            try {
                output = ObjectOutputStream(FileOutputStream(file))
                output.writeObject(obj)
            } catch (e: IOException) {
                mainActivity.showErrorPopup("Не удалось сохранить данные")
            } finally {
                output?.close()
            }
        }

        fun load(file: File): Me? {
            var input: ObjectInputStream? = null
            var me: Me? = null
            try {
                input = ObjectInputStream(FileInputStream(file))
                me = input.readObject() as Me?
            } catch (e: IOException) {
                mainActivity.showErrorPopup("Не удалось загрузить данные")
            } finally {
                input?.close()
            }
            return me
        }
    }

    private fun showErrorPopup(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

    private fun initialize() {
        val file = File(filesDir, "me_store")
        if (file.exists()) {
            me = load(file)
        } else {
            val intent = Intent(this, InviteActivity::class.java)
            startActivity(intent)
        }
    }

    fun setInfo(item: MenuItem) = startActivity(Intent(this, Settings::class.java))
}