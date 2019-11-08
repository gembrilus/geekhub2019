package iv.nakonechnyi.newsfeeder.preferences

import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.util.AttributeSet
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import androidx.preference.DialogPreference
import iv.nakonechnyi.newsfeeder.R

class DatePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    init {
        summaryProvider = SimpleSummaryProvider.instance
    }

    var date: String? = null
        set(value) {
            val changed = !TextUtils.equals(value, date)
            if (changed) {
                field = value
                persistString(value)
                notifyChanged()
            }
        }
    private var mSummary: String? = null
    private val mListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (getKey() == key) {
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

    override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?) {
        if (restorePersistedValue) {
            date = getPersistedString(null)
        } else {
            date = defaultValue as String
            persistString(date)
        }
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int) = a?.getString(index)

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

    override fun setSummary(summary: CharSequence?) {
        if (summary == null && mSummary != null) {
            mSummary = null
        } else if (summary != null && summary != mSummary) {
            mSummary = summary.toString()
        }
    }

    override fun getSummary(): CharSequence? {
            return summaryProvider?.provideSummary(this)
    }


    private class SavedState : BaseSavedState {

        internal var value: String? = null

        constructor(superState: Parcelable) : super(superState)
        constructor(source: Parcel) : super(source) {
            value = source.readString()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeString(value)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    class SimpleSummaryProvider private constructor() : SummaryProvider<DatePreference> {

        override fun provideSummary(preference: DatePreference): CharSequence? {
            return if (TextUtils.isEmpty(preference.date)) {
                preference.context.getString(R.string.date_not_set)
            } else {
                preference.date
            }
        }

        companion object {

            private var sSimpleSummaryProvider: SimpleSummaryProvider? = null
            val instance: SimpleSummaryProvider
                get() {
                    if (sSimpleSummaryProvider == null) {
                        sSimpleSummaryProvider = SimpleSummaryProvider()
                    }
                    return sSimpleSummaryProvider!!
                }
        }
    }

}