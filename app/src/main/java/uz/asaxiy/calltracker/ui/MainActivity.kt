package uz.asaxiy.calltracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import uz.asaxiy.calltracker.R
import uz.asaxiy.calltracker.databinding.ActivityMainBinding
import uz.asaxiy.calltracker.databinding.DialogAuthBinding
import uz.asaxiy.calltracker.databinding.DialogErrorBinding
import uz.asaxiy.calltracker.util.MyLocalStorage
import uz.asaxiy.calltracker.util.text

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private lateinit var viewModel: UploadCallsViewModel
    private var webViewUrl = MyLocalStorage.url!!

    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUser()
        requestPermission()
    }

    private fun checkUser() {
        if (MyLocalStorage.userPhoneNumber!!.isNotEmpty()) {
            loadWebView()
        } else {
            openAuthDialog()
//            webViewUrl = "https://lawyer.abrand.uz/"
//            MyLocalStorage.url = "https://lawyer.abrand.uz/"
//            MyLocalStorage.userPhoneNumber = "-1"
//            loadWebView()
        }
    }

    private fun openAuthDialog() {
        val dialog = AlertDialog.Builder(this)
        val view = DialogAuthBinding.inflate(layoutInflater)
        val alertDialog = dialog.create()
        alertDialog.setView(view.root)
        alertDialog.setCancelable(false)
        alertDialog.show()

        view.btn.setOnClickListener {
            val url = view.url.text()
            val phone = view.phone.text()

            if (phone.isNotEmpty() && url.isNotEmpty()) {
                webViewUrl = url
                MyLocalStorage.url = url
                MyLocalStorage.userPhoneNumber = phone
                alertDialog.cancel()
                loadWebView()
            }
        }
    }

    private fun loadWebView() {
        val url = webViewUrl
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
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {

            }
        }
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                viewModel.getCallHistory(this)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                permissionDialog("Please, accept permission")
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CALL_LOG
                )
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), 44
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            // Permission is granted. Continue the action or workflow
            // in your app.
            if(!checkPermissions()) requestPermissions()
            viewModel.getCallHistory(this)
        } else {
            permissionDialog("Please, accept permission")
            // Explain to the user that the feature is unavailable because
            // the feature requires a permission that the user has denied.
            // At the same time, respect the user's decision. Don't link to
            // system settings in an effort to convince the user to change
            // their decision.
        }
        return

    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher. You can use either a val, as shown in this snippet,
    // or a lateinit var in your onAttach() or onCreate() method.
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                viewModel.getCallHistory(this)
            } else {
                permissionDialog("Permission denied")
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    private fun permissionDialog(error: String = "") {
        val dialog = AlertDialog.Builder(this, R.style.RoundedCornersDialog)
        val view = DialogErrorBinding.inflate(layoutInflater)
        val alertDialog = dialog.create()
        alertDialog.setView(view.root)
        alertDialog.setCancelable(false)
        alertDialog.show()
        view.title.text = error
        view.close.setOnClickListener {
            alertDialog.cancel()
            requestPermissionLauncher.launch(
                Manifest.permission.READ_CALL_LOG
            )
        }


    }

    private fun initVM() {
        viewModel = ViewModelProvider(this)[UploadCallsViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}