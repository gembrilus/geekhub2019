package iv.nakonechnyi.specialnumbers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import iv.nakonechnyi.specialnumbers.pager.Factory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        pager.apply {
            adapter = PagerAdapter()
        }

        tabs.setupWithViewPager(pager)
    }

    private inner class PagerAdapter : FragmentStatePagerAdapter(
        supportFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getItem(position: Int): Fragment = Factory.creator(position)
        override fun getCount(): Int = Factory.count()
        override fun getPageTitle(position: Int): CharSequence? {
            return (getItem(position) as Factory).name
        }
    }
}
