package iv.nakonechnyi.newsfeeder

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iv.nakonechnyi.newsfeeder.model.Article
import iv.nakonechnyi.newsfeeder.model.DataViewModel
import iv.nakonechnyi.newsfeeder.net.NewsLoader
import kotlinx.android.synthetic.main.list_fragment.view.*
import java.lang.IllegalArgumentException

class ListFragment : Fragment(), LoaderManager.LoaderCallbacks<List<Article>> {

    companion object {
        fun newInstance() = ListFragment()
    }

    private lateinit var viewModel: DataViewModel
    private lateinit var fragmentView: View
    private lateinit var recyclerView: RecyclerView
    private var callback: Callbacks? = null
    private val orientation: Int
        get() = if (resources.getBoolean(R.bool.recycler_orientation))
            RecyclerView.VERTICAL
        else
            RecyclerView.HORIZONTAL

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.list_fragment, container, false)
        return fragmentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        val netManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = netManager.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) {
            LoaderManager.getInstance(this).initLoader(0, null, this)
        } else {

        }

        recyclerView = fragmentView.recycle_fragment.apply {
            layoutManager = LinearLayoutManager(requireContext(), orientation, false)
            adapter = RecyclerAdapter(this@ListFragment, viewModel.getData()).apply {
                onClickListener = object : OnItemClickListener {
                    override fun onItemClick(url: String) {
                        callback?.onArticleSelected(url)
                    }
                }

            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Article>> {
        return NewsLoader(
            requireContext(),
            getUrlWithArgs(getString(R.string.news_api_baseUrl))
        )
    }

    override fun onLoadFinished(loader: Loader<List<Article>>, news: List<Article>?) {
        news?.let { viewModel.setData(it) }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onLoaderReset(loader: Loader<List<Article>>) {
        viewModel.setData(listOf())
    }

    @Throws(IllegalArgumentException::class)
    private fun getUrlWithArgs(baseUrl: String): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val apiKey = getString(R.string.news_apiKey)
        val typeOfNews = sharedPreferences.getString(
            getString(R.string.type_of_news_key),
            getString(R.string.type_of_news_defaultValue)
        )!!
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
                require(!(country == "" && category == "" && keywords == "")) { "Вы должны указать фильтр запроса" }
                append("$baseUrl$typeOfNews?")
                if (country != "" || country != getString(R.string.all_country_item_name)) append("country=$country&")
                if (category != "") append("category=$category&")
                if (keywords != "") append("q=$keywords&")
                if (count != "") append("pageSize=$count&")
                append("apiKey=$apiKey")
            }
            else {
                require(!(country == "" && keywords == "" && dateFrom == "" && dateTo == "")) { "Вы должны указать фильтр запроса" }
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
