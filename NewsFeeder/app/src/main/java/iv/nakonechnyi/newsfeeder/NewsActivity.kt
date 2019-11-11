package iv.nakonechnyi.newsfeeder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import iv.nakonechnyi.newsfeeder.model.URL_KEY

class NewsActivity : AppCompatActivity() {

    private val mUrl get() = intent.getStringExtra(URL_KEY)
    private val isDualPane get() = resources.getBoolean(R.bool.dual_pane)
    private val newsFragment get() = NewsFragment.newInstance(mUrl)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_news_fragment, newsFragment)
                .commitNow()
        }

        if (isDualPane){
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(URL_KEY, mUrl)
            })
            finish()
        }
    }
}
