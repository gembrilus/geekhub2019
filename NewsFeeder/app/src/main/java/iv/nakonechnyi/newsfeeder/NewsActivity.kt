package iv.nakonechnyi.newsfeeder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_news_fragment, NewsFragment.getInstance())
                .commitNow()
        }
    }
}
