package iv.nakonechnyi.aboutme

import android.os.Bundle
import iv.nakonechnyi.aboutme.fragments.AdditionalInfoFragment

class AdditionalInfoActivity : MainActivity() {

    override fun createFragment() = AdditionalInfoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.title_add_info)

    }
}