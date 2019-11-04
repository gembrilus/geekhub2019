package iv.nakonechnyi.newsfeeder

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.preference.PreferenceDialogFragmentCompat
import java.text.SimpleDateFormat
import java.util.*

class DatePreferenceDialogFragmentCompat(key: String) :
    PreferenceDialogFragmentCompat() {

    private val DATE_STRING = "DatePreferenceDialog.DATE"

    private var date: String? = null

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        builder.apply {

            val view = inflater?.inflate(R.layout.date_picker_layout, null) as DatePicker
            setCalendar(view)
            setView(view)
            setPositiveButton(android.R.string.ok) { _, _ ->

                val calendar = with(view) {
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                }
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            }
            setNegativeButton(android.R.string.cancel) { _, _ ->
                this@DatePreferenceDialogFragmentCompat.dialog?.cancel()
            }
        }
        return builder.create()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val pref = preference as DatePreference
            if (pref.callChangeListener(this.date)) {
                pref.date = this.date
            }
        }
    }

    private fun setCalendar(view: DatePicker){
        if (date == null){
            with(Calendar.getInstance()){
                view.init(
                    get(Calendar.YEAR),
                    get(Calendar.MONTH),
                    get(Calendar.DAY_OF_MONTH),
                    null
                )
            }
        } else {
            val mDate = date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it) }
            val calendar = mDate?.let { Calendar.getInstance().apply { time = it } }
            calendar?.let {
                with(it) {
                    view.init(
                        get(Calendar.YEAR),
                        get(Calendar.MONTH),
                        get(Calendar.DAY_OF_MONTH),
                        null
                    )
                }
            }
        }
    }
}