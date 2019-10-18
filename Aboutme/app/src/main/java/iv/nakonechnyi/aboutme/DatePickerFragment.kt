package iv.nakonechnyi.aboutme

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.part_main_info.*
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        activity?.tw_birthday?.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    override fun onCancel(dialog: DialogInterface) {
        if(activity?.tw_birthday?.text == "") {
            Toast.makeText(activity, getString(R.string.do_not_pick_birthday), Toast.LENGTH_SHORT).show()
        }
        super.onCancel(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(context!!,this, year, month, day)
    }
}