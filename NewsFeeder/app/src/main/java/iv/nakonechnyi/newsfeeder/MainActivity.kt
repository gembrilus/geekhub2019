package iv.nakonechnyi.newsfeeder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.*
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import iv.nakonechnyi.newsfeeder.model.*


class MainActivity : AppCompatActivity(), Callbacks {

    /********************************Variables***************************************/
    /********************************************************************************/
    private val mUrl get() = UrlHolder.url
    private var mPosition: Int = 0
    private val isDualPane get() = resources.getBoolean(R.bool.dual_pane)
    private val newsFragment get() = mUrl?.let { NewsFragment.newInstance(it) } ?: EmptyFragment()
    private val listFragment get() = ListFragment.newInstance(mPosition)

    private lateinit var newsService: NewsService
    private val mConnection: ServiceConnection = NewsServiceConnection()
    private var isBound = false


    /********************************Callbacks***************************************/
    /********************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        if (savedInstanceState == null) {
            putFragment(R.id.container_list_fragment, listFragment)
        }
    }


    override fun onStart() {
        super.onStart()
        makeBinding()
    }


    override fun onResume() {
        super.onResume()
        showMainContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onArticleSelected(url: String, position: Int) {
        mPosition = position
        UrlHolder.url = url
        if (!isDualPane) {
            startActivity(Intent(this, NewsPagerActivity::class.java))
        } else {
            putFragment(R.id.container_news_fragment, newsFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(mConnection)
            isBound = false
        }
    }


    /********************************Private methods*********************************/
    /********************************************************************************/

    private fun showMainContent() {

        if (UrlHolder.url != null && !isDualPane) {
            startActivity(Intent(this, NewsPagerActivity::class.java))
        }

        if (isDualPane) {
            putFragment(R.id.container_news_fragment, newsFragment)
        }
    }


    private fun putFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(id, fragment)
                .commitNow()
        }
    }

    private fun makeBinding() {
        bindService(
            Intent(this, NewsService::class.java),
            mConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    /********************************Nested classes**********************************/
    /********************************************************************************/


    class EmptyFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.empty_fragment_layout, container, false)
    }


    private inner class NewsServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }

        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            newsService = (binder as NewsService.NewsBinder).getService()
            isBound = true
        }
    }

}