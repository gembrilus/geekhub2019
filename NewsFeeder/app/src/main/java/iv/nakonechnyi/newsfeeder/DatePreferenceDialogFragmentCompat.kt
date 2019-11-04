package iv.nakonechnyi.newsfeeder

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.preference.PreferenceDialogFragmentCompat
import kotlinx.android.synthetic.main.date_picker_layout.*
import java.text.SimpleDateFormat
import java.util.*

class DatePreferenceDialogFragmentCompat(private val listener: NoticeDialogListener) : PreferenceDialogFragmentCompat(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        builder.apply {
            setView(inflater?.inflate(R.layout.date_picker_layout, null))
            setPositiveButton(android.R.string.ok) { _, _ ->

                val calendar = with(date_picker) {
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                }
                val stringDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                listener.onDialogPositiveClick(stringDate)
            }
            setNegativeButton(android.R.string.cancel){_, _ ->
                this@DatePreferenceDialogFragmentCompat.dialog?.cancel()
            }
        }
        return builder.create()
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && mClickedDialogEntryIndex >= 0) {
            val value = mEntryValues[mClickedDialogEntryIndex].toString()
            val preference = getListPreference()
            if (preference.callChangeListener(value)) {
                preference.setValue(value)
            }
        }
    }

    interface NoticeDialogListener{
        fun onDialogPositiveClick(date: String)
    }
}