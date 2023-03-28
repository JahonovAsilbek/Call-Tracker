package uz.asaxiy.calltracker.ui

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import uz.asaxiy.calltracker.databinding.ActivityMain2Binding
import uz.asaxiy.calltracker.util.gone

class WebViewActivity : AppCompatActivity() {

    private var _binding: ActivityMain2Binding? = null
    private val binding: ActivityMain2Binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        loadWebView()

    }

    private fun loadWebView() {
        val url = "https://asaxiy.uz/uz"
        val extraHeaders: HashMap<String, String> = HashMap()
        extraHeaders["Content-Type"] = "application/x-www-from-urlencoded"

        binding.webView.loadUrl(url, extraHeaders)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Log.d("AAAA", "onReceivedError: $errorCode")
                Log.d("AAAA", "onReceivedError: $description")
                Log.d("AAAA", "onReceivedError: $failingUrl")
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url == "https://asaxiy.uz/?octo_status=succeeded" || url.startsWith("https://pay2.octo.uz")) {
//                    findNavController().popBackStack()
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.gone()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}