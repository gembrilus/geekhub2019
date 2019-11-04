package iv.nakonechnyi.newsfeeder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_news.view.*
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.os.Build





class NewsFragment : Fragment() {

    companion object {
        fun getInstance() = NewsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentVew = inflater.inflate(R.layout.fragment_news, container, false)
        val url = requireActivity().intent.getStringExtra("URL") ?: arguments?.getString("URL")
        fragmentVew.web_view.apply {
            setInitialScale(resources.getInteger(R.integer.web_page_scale))
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
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(url)
        }
        return fragmentVew
    }

}