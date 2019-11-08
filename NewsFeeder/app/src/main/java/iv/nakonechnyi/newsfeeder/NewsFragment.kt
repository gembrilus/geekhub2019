package iv.nakonechnyi.newsfeeder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_news.view.*
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.os.Build
import android.webkit.WebSettings

class NewsFragment : Fragment() {

    private val URL_KEY = "URL"
    private lateinit var webView: WebView

    companion object {
        fun getInstance() = NewsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_news, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = requireActivity().intent.getStringExtra(URL_KEY) ?: arguments?.getString(URL_KEY)

        webView = view.web_view
        with(webView) {
            setInitialScale(resources.getInteger(R.integer.web_page_scale))
            settings.builtInZoomControls = true
            settings.loadWithOverviewMode = true
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        view.loadUrl(request.url.toString())
                        return true
                    }
                }
            } else {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return true
                    }
                }
            }
            loadUrl(url)
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

}