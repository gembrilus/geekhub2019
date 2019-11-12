package iv.nakonechnyi.newsfeeder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import iv.nakonechnyi.newsfeeder.model.ArticleViewModel
import iv.nakonechnyi.newsfeeder.model.URL_KEY
import kotlinx.android.synthetic.main.activity_news_pager.*

class NewsPagerActivity : AppCompatActivity() {

    private var mUrl: String? = null
    private val isDualPane get() = resources.getBoolean(R.bool.dual_pane)
    private val pos get() = ArticleViewModel.getArticles().indexOf(ArticleViewModel.getArticles().find { it.url == mUrl })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_pager)

        mUrl = intent.getStringExtra(URL_KEY)

        pager.apply {
            adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
                override fun getItem(position: Int): Fragment {
                    mUrl = ArticleViewModel.getArticles()[position].url
                    return NewsFragment.newInstance(mUrl)
                }
                override fun getCount(): Int = ArticleViewModel.getArticles().size
            }

            setCurrentItem(pos, false)
        }

        if (isDualPane){
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(URL_KEY, mUrl)
            })
            finish()
        }
    }
}
