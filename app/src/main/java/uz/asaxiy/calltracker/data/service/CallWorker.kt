package uz.asaxiy.calltracker.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.provider.CallLog
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.asaxiy.calltracker.R
import uz.asaxiy.calltracker.data.remote.ApiClient
import uz.asaxiy.calltracker.domain.dto.Call
import uz.asaxiy.calltracker.domain.dto.CallRequest
import uz.asaxiy.calltracker.util.MyLocalStorage
import uz.asaxiy.calltracker.util.formatPhone
import java.lang.Long
import java.util.*
import kotlin.Int
import kotlin.String

class CallWorker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
//        MediaPlayer.create(context, R.raw.sound).start()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED)
            getCallLogs()
        return Result.success()
    }

    private suspend fun getCallLogs() {
        val callLogs = ArrayList<Call>()
        var hasNext = true


        withContext(Dispatchers.IO) {
            val managedCursor: Cursor? = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null, null, null, null
            )
            if (managedCursor != null) {
                val number: Int = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
                val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
                val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)
                val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
                while (managedCursor.moveToNext() && hasNext) {
                    val phNumber: String = managedCursor.getString(number).formatPhone()
                    val callType: String = managedCursor.getString(type)
                    val callDate: String = managedCursor.getString(date)
                    val callDayTime = Date(Long.valueOf(callDate))
                    val callDuration: Int? = managedCursor.getString(duration).toIntOrNull()
//                    var dir = ""
                    val dircode = callType.toInt()
//                    when (dircode) {
//                        CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
//                        CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
//                        CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
//                    }

                    val call = Call(
                        date = callDayTime.time,
                        number = phNumber,
                        duration = callDuration ?: 0,
                        type = dircode
                    )

                    val currentTime = System.currentTimeMillis()

                    if (currentTime - callDayTime.time < 86400000) {
                        if (phNumber.isNotEmpty())
                            callLogs.add(call)
                    } else
                        hasNext = false
                }
                managedCursor.close()

                uploadToServer(callLogs)

                hasNext = true
            }
        }

    }

    private suspend fun uploadToServer(callLogs: ArrayList<Call>) {
        val apiService = ApiClient.apiService
        val response = apiService.postCall(
            CallRequest(MyLocalStorage.userPhoneNumber ?: "", callLogs)
        )
        if (response.isSuccessful) {
            Log.d("AAAA", "uploadToServer: uploaded")
        } else {
            Log.d("AAAA", "service working: ${response.errorBody()}")
        }

    }

}