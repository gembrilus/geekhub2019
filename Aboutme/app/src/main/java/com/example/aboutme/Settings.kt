package com.example.aboutme

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        group1_minimize.setOnClickListener {
            if(hided_group1.isVisible) hided_group1.visibility = View.GONE
            else hided_group1.visibility = View.VISIBLE
/*            if(ll_group1.getChildAt(1).isVisible) {
                for (i in 1 until ll_group1.size) {
                    val v = ll_group1.getChildAt(i)
                    v.visibility = View.GONE
                }
            }
            else{
                for (i in 1 until ll_group1.size) {
                    val v = ll_group1.getChildAt(i)
                    v.visibility = View.VISIBLE
                }
            }*/
        }
    }

}
