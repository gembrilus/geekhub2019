package iv.nakonechnyi.newsfeeder

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import iv.nakonechnyi.newsfeeder.preferences.DatePreference
import iv.nakonechnyi.newsfeeder.preferences.DatePreferenceDialogFragmentCompat
import java.lang.IllegalArgumentException

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
        val f = when (pref) {
            is DatePreference -> DatePreferenceDialogFragmentCompat(pref.key)
            is EditTextPreference -> EditTextPreferenceDialogFragmentCompat.newInstance(pref.key)
            is ListPreference -> ListPreferenceDialogFragmentCompat.newInstance(pref.key)
            is DropDownPreference -> MultiSelectListPreferenceDialogFragmentCompat.newInstance(pref.key)
            else -> throw IllegalArgumentException(
                "Cannot display dialog for an unknown Preference type: "
                        + pref?.javaClass?.simpleName
                        + ". Make sure to implement onPreferenceDisplayDialog() to handle "
                        + "displaying a custom dialog for this Preference."
            )
        }

        f?.setTargetFragment(caller, 0)
        f?.show(supportFragmentManager, "FRAGMENT")
        return true
    }

    class HeaderFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceChangeListener {

        override fun onPreferenceChange(pref: Preference?, value: Any?): Boolean {
            val nValue = value as String
            if (pref?.key == getString(R.string.type_of_news_key)) {
                setPreferencesVisibility()
            }
            when (pref) {
                is DropDownPreference -> {
                    val prefIndex = pref.findIndexOfValue(nValue)
                    if (prefIndex >= 0) {
                        val labels = pref.entries
                        pref.summary = labels[prefIndex]
                    }
                }
                else -> pref?.summary = nValue
            }
            return true
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

            setPreferencesFromResource(R.xml.preferences, rootKey)
            initialize()
            val count = findPreference<EditTextPreference>(getString(R.string.count_key))
            count?.setOnBindEditTextListener { it.inputType = InputType.TYPE_CLASS_NUMBER }

        }

        private fun initialize() {
            setPreferencesVisibility()
            preferenceManager.sharedPreferences.all.entries.forEach {
                val pref = findPreference<Preference>(it.key)
                pref?.let { p -> p.onPreferenceChangeListener = this }
                when (pref) {
                    is DropDownPreference -> pref.summary = pref.entry
                    is EditTextPreference -> pref.summary = pref.text
                    is DatePreference -> pref.summary = pref.date
                }

            }
        }

        private fun setPreferencesVisibility() {

            val typeOfNews =
                findPreference<DropDownPreference>(getString(R.string.type_of_news_key))
            val isForTopNews = typeOfNews?.value == getString(R.string.type_of_news_defaultValue)

            val countries = findPreference<DropDownPreference>(getString(R.string.countries_key))
            val categories = findPreference<DropDownPreference>(getString(R.string.categories_key))
            val dateFrom = findPreference<DatePreference>(getString(R.string.date_from_key))
            val dateTo = findPreference<DatePreference>(getString(R.string.date_to_key))
            val sortBy = findPreference<DropDownPreference>(getString(R.string.sortBy_key))

            val title = if(isForTopNews) getString(R.string.countries_title) else "Languages"
            countries?.title = title
            categories?.isVisible = isForTopNews
            dateFrom?.isVisible = !isForTopNews
            dateTo?.isVisible = !isForTopNews
            sortBy?.isVisible = !isForTopNews
        }

    }
}
