package uz.asaxiy.calltracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uz.asaxiy.calltracker.R
import uz.asaxiy.calltracker.domain.dto.Call
import uz.asaxiy.calltracker.databinding.ActivityMainBinding
import uz.asaxiy.calltracker.databinding.DialogErrorBinding
import uz.asaxiy.calltracker.ui.adapters.CallAdapter
import uz.asaxiy.calltracker.util.Resource
import uz.asaxiy.calltracker.util.invisible
import uz.asaxiy.calltracker.util.visible


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private lateinit var viewModel: UploadCallsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()

        observe()
        setView()

    }

    private fun setView() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_upload -> {
                    viewModel.getCallHistory(context = this, uploadToServer = true)
                    true
                }
                R.id.action_refresh -> {
                    viewModel.getCallHistory(context = this, uploadToServer = false)
                    true
                }
                else -> false
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.uploadState.collect { state ->
                when (state) {
                    is Resource.NoInternet -> {
                        handleError("No internet connection")
                        binding.progressBar.invisible()
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visible()
                    }
                    is Resource.Error<*> -> {
                        handleError(state.message.toString())
                        binding.progressBar.invisible()
                    }
                    is Resource.Success<*> -> {
                        binding.progressBar.invisible()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.callHistory.collect { state ->
                when (state) {
                    is Resource.NoInternet -> {
                        handleError("No internet connection")
                        binding.progressBar.invisible()
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visible()
                    }
                    is Resource.Error<*> -> {
                        handleError(state.message.toString())
                        binding.progressBar.invisible()
                    }
                    is Resource.Success<*> -> {
                        binding.progressBar.invisible()

                        val adapter = CallAdapter(state.data as List<Call>)
                        binding.rv.adapter = adapter
                    }
                }
            }
        }
    }

    private fun initVM() {
        viewModel = ViewModelProvider(this)[UploadCallsViewModel::class.java]
    }

    private fun handleError(error: String = "") {
        val dialog = AlertDialog.Builder(this, R.style.RoundedCornersDialog)
        val view = DialogErrorBinding.inflate(layoutInflater)
        val alertDialog = dialog.create()
        alertDialog.setView(view.root)
        alertDialog.setCancelable(false)
        alertDialog.show()
        view.title.text = error
        view.close.setOnClickListener { alertDialog.cancel() }

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}