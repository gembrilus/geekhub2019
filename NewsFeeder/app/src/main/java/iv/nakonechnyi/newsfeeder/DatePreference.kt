package iv.nakonechnyi.newsfeeder

import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.util.AttributeSet
import java.text.SimpleDateFormat
import java.util.*
import android.os.Parcel
import android.os.Parcelable
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class DatePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

/*    init {
        dialogTitle = "Pick a date"
        dialogIcon = null
        setViewId(R.layout.date_picker_layout)
        positiveButtonText = "OK"
        negativeButtonText = "CANCEL"
    }*/


    private val DEFAULT_VALUE get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private var date = DEFAULT_VALUE
    private val mListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if(getKey() == key){
            date = prefs.getString(key, date)
        }
    }

    override fun onAttached() {
        super.onAttached()
        sharedPreferences.registerOnSharedPreferenceChangeListener(mListener)
    }

    override fun onDetached() {
        super.onDetached()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mListener)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
            date = defaultValue as String
            persistString(date)
        }

    override fun onGetDefaultValue(a: TypedArray?, index: Int) = a?.getString(index) ?: DEFAULT_VALUE

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) {
            return superState
        }
        val myState = SavedState(superState)

        myState.value = date
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {

        if (state == null || state.javaClass != SavedState::class.java) {
            super.onRestoreInstanceState(state)
            return
        }

        val myState = state as SavedState
        super.onRestoreInstanceState(myState.superState)
        date = myState.value
    }


    private class SavedState : BaseSavedState {

        internal var value: String? = null

        constructor(superState: Parcelable) : super(superState)
        constructor(source: Parcel) : super(source){
            value = source.readString()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(value)
        }

        companion object {

            @JvmField val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}