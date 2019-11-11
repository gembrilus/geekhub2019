package iv.nakonechnyi.newsfeeder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import iv.nakonechnyi.newsfeeder.model.Article
import iv.nakonechnyi.newsfeeder.model.DataViewModel
import iv.nakonechnyi.newsfeeder.model.POS_KEY
import iv.nakonechnyi.newsfeeder.net.NewsLoaderHelper
import kotlinx.android.synthetic.main.list_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    SharedPreferences.OnSharedPreferenceChangeListener, NewsLoaderHelper.NetworkExceptionHandler {

    companion object {

        fun newInstance(pos: Int?) = ListFragment().apply {
            pos?.let {
                arguments = Bundle().apply {
                    putInt(POS_KEY, it)
                }
            }
        }
    }

    /*-------------------------------Variables---------------------------------*/

    private lateinit var viewModel: DataViewModel
    private lateinit var fragmentView: View
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var recycleLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var callback: Callbacks? = null
    private val mUrl get() = getUrlWithArgs(getString(R.string.news_api_baseUrl))

    private val orientation: Int
        get() = if (resources.getBoolean(R.bool.recycler_orientation))
            RecyclerView.VERTICAL
        else
            RecyclerView.HORIZONTAL

    /*----------------------------Callbacks methods------------------------------*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callbacks
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        loadContent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.list_fragment, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        view.swipe_to_refresh.apply {
            isRefreshing = true
            setOnRefreshListener(this@ListFragment)
        }

        recycleLayoutManager = LinearLayoutManager(requireContext(), orientation, false)
        recyclerAdapter = RecyclerAdapter().apply {
            onClickListener = object : RecyclerAdapter.OnItemClickListener {
                override fun onItemClick(url: String, position: Int) {
                    if (orientation == RecyclerView.HORIZONTAL) {
                        recycleLayoutManager.smoothScrollToPosition(
                            recyclerView,
                            RecyclerView.State(),
                            20
                        )
                    }
                    callback?.onArticleSelected(url, position)
                }
            }
        }

        recyclerView = fragmentView.recycle_fragment.apply {
            adapter = recyclerAdapter
            layoutManager = recycleLayoutManager
        }

        viewModel.data.observe(
            this,
            Observer { list: List<Article> -> recyclerAdapter.submit(list) })
    }

    override fun onRefresh() {
        loadContent()
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        fragmentView.swipe_to_refresh.isRefreshing = true
        loadContent()
    }

    override fun onNoNetworkConnection() {
        GlobalScope.launch(Dispatchers.Main) {
            fragmentView.background = resources.getDrawable(R.drawable.no_internet_connection)
        }
    }

    /*----------------------Additionl functions-----------------------*/


    private fun loadContent() = GlobalScope.launch(Dispatchers.Main) {
        fragmentView.background = resources.getDrawable(R.color.newsfeeder_bg_color)
        val list =
            NewsLoaderHelper(mUrl, this@ListFragment).fetchNews()
        viewModel.data.value = list
        fragmentView.swipe_to_refresh.isRefreshing = false
    }

    private fun getUrlWithArgs(baseUrl: String): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val apiKey = getString(R.string.news_apiKey)
        val typeOfNews = sharedPreferences.getString(
            getString(R.string.type_of_news_key),
            getString(R.string.type_of_news_defaultValue)
        )
        val country = sharedPreferences.getString(getString(R.string.countries_key), "")
        val language = sharedPreferences.getString(getString(R.string.languages_key), "")
        val category = sharedPreferences.getString(getString(R.string.categories_key), "")
        val keywords = sharedPreferences.getString(getString(R.string.keywords_key), "")
        val count = sharedPreferences.getString(getString(R.string.count_key), "")
        val dateFrom = sharedPreferences.getString(getString(R.string.date_from_key), "")
        val dateTo = sharedPreferences.getString(getString(R.string.date_to_key), "")
        val sortBy = sharedPreferences.getString(getString(R.string.sortBy_key), "")

        return buildString {
            if (typeOfNews == getString(R.string.type_of_news_defaultValue)) {
                require(!(country == "" && category == "" && keywords == "")) { getString(R.string.require_news_filter) }
                append("$baseUrl$typeOfNews?")
                if (country != "") append("country=$country&")
                if (category != "") append("category=$category&")
                if (keywords != "") append("q=$keywords&")
                if (count != "") append("pageSize=$count&")
                append("apiKey=$apiKey")
            } else {
                require(!(country == "" && keywords == "" && dateFrom == "" && dateTo == "")) {
                    getString(
                        R.string.require_news_filter
                    )
                }
                append("$baseUrl$typeOfNews?")
                if (keywords != "") append("q=$keywords&")
                if (dateFrom != "" || dateFrom != getString(R.string.date_not_set)) append("from=$dateFrom&")
                if (dateTo != "" || dateTo != getString(R.string.date_not_set)) append("to=$dateTo&")
                if (language != "" || language != getString(R.string.all_lang_item_name)) append("language=$language&")
                if (sortBy != "") append("sortBy=$sortBy&")
                if (count != "") append("pageSize=$count&")
                append("apiKey=$apiKey")
            }
        }
    }
}
