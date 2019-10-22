package iv.nakonechnyi.aboutme

import android.os.Bundle
import iv.nakonechnyi.aboutme.fragments.ShortInfoFragment

class ShortInfoActivity : MainActivity(){

    override fun createFragment() = ShortInfoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
    }
}