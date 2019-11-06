package iv.nakonechnyi.newsfeeder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity(), Callbacks {

    private val URL_KEY = "URL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_list_fragment, ListFragment.newInstance())
                .commitNow()
        }
    }

    override fun onArticleSelected(url: String) {
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_news_fragment, fragment)
                .commitNow()
        }
    }
}
