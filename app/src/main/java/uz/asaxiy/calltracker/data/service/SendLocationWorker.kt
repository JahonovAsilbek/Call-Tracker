package uz.asaxiy.calltracker.data.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.asaxiy.calltracker.data.remote.ApiClient
import uz.asaxiy.calltracker.util.MyLocalStorage
import java.util.*
import java.util.concurrent.TimeUnit

class SendLocationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {

        if (checkPermissions()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

            locationRequest = LocationRequest().apply {
                interval = TimeUnit.SECONDS.toMillis(60)
                fastestInterval = TimeUnit.SECONDS.toMillis(30)
                maxWaitTime = TimeUnit.MINUTES.toMillis(2)

                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {

                    super.onLocationResult(locationResult)
                    locationResult.lastLocation?.let {
                        val lat = it.latitude
                        val long = it.longitude

                        val apiService = ApiClient.apiService

                        if (MyLocalStorage.userPhoneNumber!!.isNotEmpty())
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = apiService.postLocation(userId = MyLocalStorage.userPhoneNumber!!, lat, long)
                                if (response.isSuccessful)
                                    Log.d("AAAA", "onLocationResult: uploaded succesfully")
                                else
                                    Log.d("AAAA", "onLocationResult: upload failed")
                            }
                    }
                }
            }
            try {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
            }
        }
        return Result.retry()
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}