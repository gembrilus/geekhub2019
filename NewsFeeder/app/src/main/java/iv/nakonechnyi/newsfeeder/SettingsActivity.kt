package iv.nakonechnyi.newsfeeder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.*

class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, HeaderFragment())
                .commit()
        }
    }

    override fun onPreferenceDisplayDialog(
        caller: PreferenceFragmentCompat,
        pref: Preference?
    ): Boolean {
        var f: DialogFragment? = null
        if (pref is DatePreference) {
            f = DatePreferenceDialogFragmentCompat(pref.key)
        } else {
            caller.onDisplayPreferenceDialog(pref)
        }
        f?.setTargetFragment(caller, 0)
        f?.show(supportFragmentManager, "FRAGMENT")
        return true
    }

    class HeaderFragment : PreferenceFragmentCompat() {


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

        }

    }
}
