package iv.nakonechnyi.aboutme

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import iv.nakonechnyi.aboutme.fragments.PagerFragment
import kotlinx.android.synthetic.main.activity_photo_show.*

class PhotoShowActivity : AppCompatActivity() {

    private val NUM_PAGES = me.photos.size-1
    private lateinit var mPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_photo_show)

        mPager = pager.apply {
            adapter = PagerAdapter(supportFragmentManager)
        }
    }

    private inner class PagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        override fun getCount() = NUM_PAGES
        override fun getItem(position: Int) =
            PagerFragment(position)
    }
}
