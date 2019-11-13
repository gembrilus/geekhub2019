package iv.nakonechnyi.newsfeeder

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import iv.nakonechnyi.newsfeeder.model.*
import iv.nakonechnyi.newsfeeder.util.PermissionHelper
import iv.nakonechnyi.newsfeeder.util.makeSimpleNotification


class MainActivity : AppCompatActivity(), Callbacks {

    /********************************Variables***************************************/
    /********************************************************************************/
    private var mUrl: String? = null
    private var mPosition: Int? = null
    private val isDualPane get() = resources.getBoolean(R.bool.dual_pane)
    private val newsFragment get() = mUrl?.let { NewsFragment.newInstance(it) } ?: EmptyFragment()
    private val listFragment get() = ListFragment.newInstance(mPosition)

    private lateinit var newsService: NewsService
    private val mConnection : ServiceConnection = NewsServiceConnection()
    private var isBound = false


    /********************************Callbacks***************************************/
    /********************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        registerNotificationChannel()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.geo_location -> {
                val helper = PermissionHelper.getPermissionHelper(this)
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    helper.requestPermission(
                        this,
                        100,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } else {
                    makeBinding()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                if (PermissionHelper.getPermissionHelper(this).isPermissionGranted(
                        permissions,
                        grantResults,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    makeBinding()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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

    override fun onStop() {
        super.onStop()
        if (isBound){
            unbindService(mConnection)
            isBound = false
        }
    }

    /********************************Private methods*********************************/
    /********************************************************************************/

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

    private fun makeBinding(){
        bindService(
            Intent(this, NewsService::class.java),
            mConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun registerNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = getString(R.string.notification_channel_description)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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

            makeSimpleNotification(baseContext, getString(R.string.notification_launch_service_title), getString(
                            R.string.notification_launch_service_text))
        }
    }

}