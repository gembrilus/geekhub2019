package iv.nakonechnyi.newsfeeder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import iv.nakonechnyi.newsfeeder.model.STORE_URL_KEY
import iv.nakonechnyi.newsfeeder.model.URL_KEY

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

        mUrl = savedInstanceState?.getString(STORE_URL_KEY) ?: intent.getStringExtra(
            URL_KEY
        )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_list_fragment, listFragment)
                .commitNow()
        }

        if (isDualPane) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_news_fragment, newsFragment)
                .commitNow()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mUrl = savedInstanceState.getString(STORE_URL_KEY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STORE_URL_KEY, mUrl)
    }

    override fun onArticleSelected(url: String, position: Int) {
        mPosition = position
        mUrl = url
        if (!isDualPane) {
            startActivity(Intent(this, NewsActivity::class.java).apply {
                putExtra(URL_KEY, url)
            })
        } else {
            supportFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.container_news_fragment, newsFragment)
                    .commitNow()
            }
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