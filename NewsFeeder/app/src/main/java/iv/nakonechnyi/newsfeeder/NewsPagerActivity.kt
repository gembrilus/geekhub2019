package iv.nakonechnyi.newsfeeder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import iv.nakonechnyi.newsfeeder.model.*
import kotlinx.android.synthetic.main.activity_news_pager.*

class NewsPagerActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    private val mUrl get() = UrlHolder.url
    private val isDualPane get() = resources.getBoolean(R.bool.dual_pane)
    private val pos get() = ArticleViewModel.getArticles().indexOf(ArticleViewModel.getArticles().find { it.url == mUrl })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_pager)

        pager.apply {
            adapter = object : FragmentStatePagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int): Fragment {
                    val url = ArticleViewModel.getArticles()[position].url
                    return NewsFragment.newInstance(url)
                }

                override fun getCount(): Int = ArticleViewModel.getArticles().size
            }
            addOnPageChangeListener(this@NewsPagerActivity)
            setCurrentItem(pos, false)
        }

        if (isDualPane) finish()
    }

    override fun onPageScrollStateChanged(state: Int) {}
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        val article = ArticleViewModel.getArticles()[position]
        supportActionBar?.subtitle = article.title
        UrlHolder.url = article.url
    }

    override fun onBackPressed() {
        UrlHolder.url = null
        super.onBackPressed()
    }
}
