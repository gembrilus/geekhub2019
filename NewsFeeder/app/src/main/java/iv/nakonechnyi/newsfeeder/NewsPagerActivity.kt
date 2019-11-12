package iv.nakonechnyi.newsfeeder

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import iv.nakonechnyi.newsfeeder.model.ArticleViewModel
import iv.nakonechnyi.newsfeeder.model.RESULT_URL_FROM_PAGER
import iv.nakonechnyi.newsfeeder.model.URL_KEY
import kotlinx.android.synthetic.main.activity_news_pager.*

class NewsPagerActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    private var mUrl: String? = null
    private val isDualPane get() = resources.getBoolean(R.bool.dual_pane)
    private val pos get() = ArticleViewModel.getArticles().indexOf(ArticleViewModel.getArticles().find { it.url == mUrl })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_pager)

        mUrl = intent.getStringExtra(URL_KEY)

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

        if (isDualPane) {
            setResult(Activity.RESULT_OK, Intent().apply { putExtra(RESULT_URL_FROM_PAGER, mUrl) })
            finish()
        }
    }

    override fun onPageScrollStateChanged(state: Int) = Unit
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
    override fun onPageSelected(position: Int) {
        val article = ArticleViewModel.getArticles()[position]
        title = article.title
        mUrl = article.url
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(RESULT_URL_FROM_PAGER, mUrl)
        })
    }
}
