package iv.nakonechnyi.aboutme.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.crashlytics.android.Crashlytics
import iv.nakonechnyi.aboutme.*
import iv.nakonechnyi.aboutme.data.Me
import iv.nakonechnyi.aboutme.util.getAge
import iv.nakonechnyi.aboutme.util.load
import iv.nakonechnyi.aboutme.util.resourceToUri
import iv.nakonechnyi.aboutme.util.save
import kotlinx.android.synthetic.main.fragment_info_short.view.*
import java.text.SimpleDateFormat
import java.util.*

class ShortInfoFragment : Fragment() {

    private val REQUEST_CODE_PHOTOSHOW = 40
    private lateinit var vShortInfo: View
    private var mDualPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        me = load(store_file)
        setHasOptionsMenu(true);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vShortInfo = inflater.inflate(R.layout.fragment_info_short, container, false).apply {
            b_info_text.setOnClickListener {
                startActivity(Intent(activity, AdditionalInfoActivity::class.java))
            }
            iv_photo.setOnClickListener {
                startActivityForResult(
                    Intent(activity, PhotoShowActivity::class.java),
                    REQUEST_CODE_PHOTOSHOW
                )
            }
        }
        setMainInfo(me)
        return vShortInfo
    }

    override fun onResume() {
        super.onResume()
        setMainInfo(me)
    }

    override fun onPause() {
        super.onPause()
        save(activity as FragmentActivity,
            store_file,
            me
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDualPane = activity?.findViewById<FrameLayout>(R.id.activity_main2).run {
            this != null && visibility == View.VISIBLE
        }

        if (mDualPane) {
            vShortInfo.b_info_text.visibility = View.GONE
            showAddInfo()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> startActivity(Intent(activity, SettingsActivity::class.java))
            R.id.crash_menu_item -> Crashlytics.getInstance().crash()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_PHOTOSHOW -> {
                    val s = data?.extras?.getString("STRING1") ?: return
                    me.photos.remove(s)
                    me.photos.add(1, s)
                    updateImage()
                }
            }
        }
    }

    private fun setMainInfo(obj: Me) {
        val age = getAge(obj.birthday)
        val s = when (age % 10) {
            1 -> getString(R.string.year)
            in 2..4 -> getString(R.string.years)
            else -> getString(R.string.years2)
        }
        updateImage()
        vShortInfo.tw_invitation.text =
            getString(R.string.welcome_message, obj.name, obj.surname, age, s)
        val date = obj.birthday
        val localDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
        vShortInfo.birthday.text = getString(R.string.was_born, localDate)
    }

    private fun updateImage() {
        with(vShortInfo.iv_photo) {
            val s = resourceToUri(activity as FragmentActivity,
                R.drawable.add
            ).toString()
            if (!me.photos.contains(s)) me.photos.add(0, s)
            if (me.photos.size <= 1) {
                setImageResource(R.drawable.anonim_photo)
            } else {
                setImageURI(Uri.parse(me.photos[1]))
            }
        }
    }

    private fun showAddInfo() {
        fragmentManager?.findFragmentById(R.id.activity_main2).also {
            it ?: fragmentManager!!.beginTransaction()
                .add(
                    R.id.activity_main2,
                    AdditionalInfoFragment()
                )
                .commit()
        }

    }
}