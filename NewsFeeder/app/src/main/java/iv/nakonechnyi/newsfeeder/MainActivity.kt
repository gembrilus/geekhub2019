package iv.nakonechnyi.newsfeeder

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import iv.nakonechnyi.newsfeeder.model.*

class MainActivity : AppCompatActivity(), Callbacks {

    private var mUrl: String? = null
    private var mPosition: Int? = null
    private val isDualPane get() = resources.getBoolean(R.bool.dual_pane)
    private val newsFragment get() = mUrl?.let { NewsFragment.newInstance(it) } ?: EmptyFragment()
    private val listFragment get() = ListFragment.newInstance(mPosition)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        if (savedInstanceState == null) {
            putFragment(R.id.container_list_fragment, listFragment)
        } else {
            mUrl = savedInstanceState.getString(STORE_URL_KEY)
        }

        if (isDualPane) {
            putFragment(R.id.container_news_fragment, newsFragment)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STORE_URL_KEY, mUrl)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_NEW_URL -> {
                    if (isDualPane) {
                        val url = data.getStringExtra(RESULT_URL_FROM_PAGER)
                        putFragment(R.id.container_news_fragment, NewsFragment.newInstance(url))
                    }
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onArticleSelected(url: String, position: Int) {
        mPosition = position
        mUrl = url
        if (!isDualPane) {
            showNewsInActivity(mUrl)
        } else {
            putFragment(R.id.container_news_fragment, newsFragment)
        }
    }

    private fun showNewsInActivity(url: String?) {
        startActivityForResult(Intent(this, NewsPagerActivity::class.java).apply {
            putExtra(URL_KEY, url)
        }, REQUEST_NEW_URL)
    }

    private fun putFragment(id: Int, fragment: Fragment){
        supportFragmentManager.apply {
            beginTransaction()
                .replace(id, fragment)
                .commitNow()
        }
    }

    class EmptyFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.empty_fragment_layout, container, false)
    }
}