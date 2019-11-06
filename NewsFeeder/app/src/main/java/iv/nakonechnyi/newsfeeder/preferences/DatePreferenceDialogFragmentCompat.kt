package iv.nakonechnyi.newsfeeder.preferences

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceDialogFragmentCompat
import java.text.SimpleDateFormat
import java.util.*

class DatePreferenceDialogFragmentCompat(key: String) :
    PreferenceDialogFragmentCompat(), DatePickerDialog.OnDateSetListener {

    private val DATE_STRING = "DatePreferenceDialog.DATE"

    private var date: String? = null
    private lateinit var dialog: DatePickerDialog

    init {
        arguments = Bundle(1).apply { putString(ARG_KEY, key) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = if (savedInstanceState == null){
            val pref = preference as DatePreference
            pref.date
        } else{
            savedInstanceState.getString(DATE_STRING)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(DATE_STRING, date)
    }

    override fun onDateSet(picker: DatePicker?, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance().apply {
            set(year, month, day)
        }
        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        onClick(dialog, DialogInterface.BUTTON_POSITIVE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        dialog = DatePickerDialog(activity as FragmentActivity,this, year, month, day).apply {
            setButton(DialogInterface.BUTTON_NEUTRAL, "Reset") { _, _ ->
                date = null
                onClick(dialog, DialogInterface.BUTTON_POSITIVE)
            }
        }
        return dialog
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val pref = preference as DatePreference
            if (pref.callChangeListener(this.date)) {
                pref.date = this.date
            }
        }
    }
}