package iv.nakonechnyi.newsfeeder

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_news.view.*
import android.os.Build
import android.webkit.*
import android.widget.Toast
import android.webkit.WebView
import iv.nakonechnyi.newsfeeder.model.URL_KEY


class NewsFragment : Fragment() {

    private lateinit var fragmentView: View
    private lateinit var webView: WebView
    private val mUrl
        get() = requireActivity().intent.getStringExtra(URL_KEY) ?: arguments?.getString(
            URL_KEY
        )

    companion object {

        fun newInstance(url: String) = NewsFragment().apply {
            arguments = Bundle().apply {
                putString(URL_KEY, url)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_news, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.web_view

        with(webView) web@{
            setInitialScale(resources.getInteger(R.integer.web_page_scale))
            settings.builtInZoomControls = true
            settings.loadWithOverviewMode = true
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            webViewClient = BrowserClient(fragmentView)
            loadUrl(mUrl)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }


    private inner class BrowserClient(private val frView: View) : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                view?.loadUrl(request?.url.toString())
                return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            frView.web_view_progress.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun onLoadResource(view: WebView?, url: String?) {
            if (view != null) {
                frView.web_view_progress.progress = view.progress
            }
            super.onLoadResource(view, url)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            frView.web_view_progress.visibility = View.GONE
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            Toast.makeText(activity, "Oh no! $description", Toast.LENGTH_SHORT).show()
        }

    }

}