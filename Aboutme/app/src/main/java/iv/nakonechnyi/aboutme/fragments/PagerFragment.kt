package iv.nakonechnyi.aboutme.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import iv.nakonechnyi.aboutme.R
import iv.nakonechnyi.aboutme.me
import kotlinx.android.synthetic.main.fragment_photo_show.view.*



class PagerFragment : Fragment(){

    companion object {
        fun newInstance(position: Int): PagerFragment {
            val args = Bundle()
            args.putInt("pos", position)
            val f = PagerFragment()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_photo_show, container, false)
        v.pager_item.apply {
            val position = arguments?.getInt("pos") ?: 0
            setImageURI(Uri.parse(me.photos[position + 1]))
            setOnClickListener {
                val s = me.photos[position + 1]
                activity?.setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra("STRING1", s)
                })
                activity?.finish()
            }
        }
        return v
    }
}