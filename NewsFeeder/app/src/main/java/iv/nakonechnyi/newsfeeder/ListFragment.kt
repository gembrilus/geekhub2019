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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iv.nakonechnyi.newsfeeder.model.Article
import iv.nakonechnyi.newsfeeder.model.DataViewModel
import iv.nakonechnyi.newsfeeder.net.NewsLoader
import kotlinx.android.synthetic.main.list_fragment.view.*

class ListFragment : Fragment(), LoaderManager.LoaderCallbacks<List<Article>> {

    companion object {
        fun newInstance() = ListFragment()
    }

    private lateinit var viewModel: DataViewModel
    private lateinit var fragmentView: View
    private lateinit var recyclerView: RecyclerView
    private var callback: Callbacks? = null

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
            layoutManager = LinearLayoutManager(requireContext(), getOrientation(), false)
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
        when(item.itemId){
            R.id.menu_item_settings -> {
                startActivity(Intent(context, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Article>> {
        return NewsLoader(
            requireContext(),
            "https://newsapi.org/v2/top-headlines?country=ua&category=science&apiKey=40f5a3b9ae834b2395af75738ccaca3b"
        )
    }

    override fun onLoadFinished(loader: Loader<List<Article>>, news: List<Article>?) {
        news?.let { viewModel.setData(it) }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onLoaderReset(loader: Loader<List<Article>>) {
        viewModel.setData(listOf())
    }

    private fun getOrientation() =
        if (resources.getBoolean(R.bool.recycler_orientation))
            RecyclerView.VERTICAL
        else
            RecyclerView.HORIZONTAL

}
