package iv.nakonechnyi.aboutme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.children
import iv.nakonechnyi.aboutme.data.map
import kotlinx.android.synthetic.main.activity_additional_info.view.*
import kotlinx.android.synthetic.main.part_social.view.*

class AdditionalInfoFragment : MainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.activity_additional_info, container, false)
            .apply {
                val map = me.map(this)
                ll_add_info.children.forEach {
                    if (map.containsKey(it))
                        (it as TextView).text = map.getValue(it)
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
}