package iv.nakonechnyi.aboutme.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import iv.nakonechnyi.aboutme.R
import iv.nakonechnyi.aboutme.data.map
import iv.nakonechnyi.aboutme.me
import kotlinx.android.synthetic.main.activity_additional_info.view.*
import kotlinx.android.synthetic.main.part_social.view.*

class AdditionalInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_additional_info, container, false)
            .apply {
                val map = me.map(this)
                fillLayout(this.ll_add_info, map)

                ib_send_email.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(tw_email_info.text.toString()))
                        putExtra(Intent.EXTRA_SUBJECT, "")
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.hello_amigo))
                        type = "message/rfc822"
                    })
                }

                ib_call_contact.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${tw_phonenumber_info.text}")
                    })
                }

                ll_social.children.forEach {
                    (it as ImageButton).apply {
                        val name = me.social[it.tag as String?] ?: ""
                        if (name == "") visibility = View.GONE
                        else {
                            visibility = View.VISIBLE
                            setOnClickListener { goToURL(name) }
                        }
                    }
                }
            }
    }

    private fun goToURL(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    private fun fillLayout(v: ViewGroup, map: Map<View, String>){
        v.children.forEach {
            when(it){
                is TextView -> {
                    if (map.containsKey(it))
                        it.text = map.getValue(it)
                }
                is ViewGroup -> fillLayout(it, map)
                }
            }
        }
}