package uz.asaxiy.calltracker

import android.app.Application
import androidx.work.*
import uz.asaxiy.calltracker.data.service.CallWorker
import uz.asaxiy.calltracker.data.service.SendLocationWorker
import uz.asaxiy.calltracker.util.MyLocalStorage
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MyLocalStorage.init(this)
        startCallWorker()
        startLocationWorker()
    }

    private fun startLocationWorker() {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<SendLocationWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }

    private fun startCallWorker() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val callWorkRequest: WorkRequest = PeriodicWorkRequestBuilder<CallWorker>(15, TimeUnit.MINUTES).setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueue(callWorkRequest)
    }
}
