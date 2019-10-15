package com.example.aboutme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.aboutme.data.Me
import com.example.aboutme.data.map
import kotlinx.android.synthetic.main.activity_additional_info.*
import kotlinx.android.synthetic.main.part_social.*


class AdditionalInfo : AppCompatActivity() {

    private lateinit var me: Me

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)
        title = getString(R.string.title_add_info)

        me = intent.getSerializableExtra("ME") as Me
        val map = me.map(this)

        ll_add_info.children.forEach {
            if(map.containsKey(it))
                (it as TextView).text = map.getValue(it)
        }

        ll_social.children.forEach {
            (it as ImageButton).apply {
                val name = me.social[it.tag as String?] ?: ""
                if (name == "") visibility = View.GONE
                else {
                    setOnClickListener {
                        goToURL(name)
                    }
                }
            }
        }
    }

    private fun goToURL(url: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}