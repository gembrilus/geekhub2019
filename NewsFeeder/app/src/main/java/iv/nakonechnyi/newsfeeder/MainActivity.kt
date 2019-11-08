package iv.nakonechnyi.newsfeeder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_news.*

class MainActivity : AppCompatActivity(), Callbacks {

    private val URL_KEY = "URL"
    private val STORE_URL_KEY = "STORE_URL"
    private var mUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_list_fragment, ListFragment.newInstance())
                .commitNow()
        } else {
            mUrl = savedInstanceState.getString(STORE_URL_KEY)
        }

        if (container_news_fragment != null) {
            val fragment = mUrl?.let {
                NewsFragment.getInstance().apply {
                    arguments = Bundle().apply {
                        putString(URL_KEY, mUrl)
                    }
                }
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container_news_fragment, fragment ?: EmptyFragment())
                .commitNow()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STORE_URL_KEY, mUrl)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mUrl = savedInstanceState.getString(STORE_URL_KEY)
    }


    override fun onArticleSelected(url: String) {
        mUrl = url
        if (!resources.getBoolean(R.bool.dual_pane)) {
            startActivity(Intent(this, NewsActivity::class.java).apply {
                putExtra(URL_KEY, url)
            })
        } else {
            val fragment = NewsFragment.getInstance().also {
                it.arguments = Bundle().apply {
                    putString(URL_KEY, url)
                }
            }
            supportFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.container_news_fragment, fragment)
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