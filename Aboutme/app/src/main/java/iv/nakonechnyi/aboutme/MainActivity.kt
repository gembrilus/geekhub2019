package iv.nakonechnyi.aboutme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import iv.nakonechnyi.aboutme.data.Me
import iv.nakonechnyi.aboutme.util.load

internal lateinit var me: Me
internal val fileName: String get() = "me_store"

abstract class MainActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        me = load(this)

        supportFragmentManager.findFragmentById(R.id.activity_main1).also {
            it ?: supportFragmentManager
                .beginTransaction()
                .add(R.id.activity_main1, createFragment())
                .commit()
        }
    }
}