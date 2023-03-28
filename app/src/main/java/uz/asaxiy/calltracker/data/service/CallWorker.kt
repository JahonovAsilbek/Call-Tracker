package uz.asaxiy.calltracker.data.service

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.asaxiy.calltracker.data.locale.AppDatabase
import uz.asaxiy.calltracker.data.locale.entity.Call
import uz.asaxiy.calltracker.data.remote.ApiClient
import uz.asaxiy.calltracker.util.NetworkHelper
import uz.asaxiy.calltracker.util.formatPhone
import java.lang.Long
import java.util.*
import kotlin.Int
import kotlin.String

class CallWorker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        getCallLogs()
        return Result.success()
    }

    private suspend fun getCallLogs() {
        val callLogs = ArrayList<Call>()

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
                while (managedCursor.moveToNext()) {
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
                        date = callDayTime.time.toString(),
                        number = phNumber,
                        duration = callDuration ?: 0,
                        type = dircode
                    )

                    if (phNumber.isNotEmpty())
                        callLogs.add(call)
                }
                managedCursor.close()


                uploadToServer(callLogs)

            }
        }

    }

    private suspend fun uploadToServer(callLogs: ArrayList<Call>) {
        val database = AppDatabase.getInstance().callDao()
        val apiService = ApiClient.apiService

        database.insertAll(callLogs)

        if (NetworkHelper(context).isNetworkConnected()) {
            database.getAllCalls().collect {
                val response = apiService.postCall(list = it)
                if (response.isSuccessful) {
                    database.deleteAllCalls()
                } else {
                    Log.d("AAAA", "service working: ${response.errorBody()}")
                }
            }
        } else {
            Log.d("AAAA", "service working: No internet connection")
        }

    }

}