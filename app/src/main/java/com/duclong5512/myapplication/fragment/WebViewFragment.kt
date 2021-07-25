package com.duclong5512.myapplication.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.duclong5512.myapplication.R

private const val URL = "URL"

class WebViewFragment : BaseFragment() {
    private var url: String? = null
    private var webView: WebView? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = view.findViewById(R.id.web_view)
        progressBar = view.findViewById(R.id.progress_bar_webview)
        return view
    }

    override fun onStart() {
        super.onStart()
        webView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }
        }

        url?.let { webView?.loadUrl(it) }
        val webSettings = webView?.settings
        if (webSettings != null) {
            webSettings.javaScriptEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView = null
    }

    override fun onBackPressed() : Boolean {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
            return true
        }
        return super.onBackPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance(url: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(URL, url)
                }
            }
    }
}