package uz.asaxiy.calltracker.ui

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.asaxiy.calltracker.data.remote.ApiClient
import uz.asaxiy.calltracker.domain.dto.Call
import uz.asaxiy.calltracker.domain.dto.CallRequest
import uz.asaxiy.calltracker.util.MyLocalStorage
import uz.asaxiy.calltracker.util.formatPhone
import java.lang.Long
import java.util.*
import kotlin.Exception
import kotlin.Int
import kotlin.String

class UploadCallsViewModel : ViewModel() {

    fun getCallHistory(context: Context) {
        val callLogs = ArrayList<Call>()
        var hasNext = true

        viewModelScope.launch(Dispatchers.IO) {

            val managedCursor: Cursor? = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null, null, null, null
            )

            try {
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
//                        var dir = ""
                        val dircode = callType.toInt()
//                        when (dircode) {
//                            CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
//                            CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
//                            CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
//                        }

                        val currentTime = System.currentTimeMillis()

                        val call = Call(
                            date = callDayTime.time,
                            number = phNumber.formatPhone(),
                            duration = callDuration ?: 0,
                            type = dircode
                        )

                        Log.d("TTTT", "getCallHistory: $call")

                        if (currentTime - callDayTime.time < 86400000) {
                            if (phNumber.isNotEmpty())
                                callLogs.add(call)
                        } else
                            hasNext = false
                    }
                    managedCursor.close()

                    uploadCalls(callLogs)

                    hasNext = true
                }
            } catch (e: Exception) {
                Log.d("AAAA", "getCallHistory VM: CATCH ${e.message}")
            }
        }
    }

    private suspend fun uploadCalls(calls: List<Call>) {
        val response = ApiClient.apiService.postCall(
            CallRequest(MyLocalStorage.userPhoneNumber ?: "", calls)
        )
        if (response.isSuccessful) {
            Log.d("AAAA", "uploadCallsVM: SUCCESS")
        } else {
            Log.d("AAAA", "uploadCalls VM: FAIL ${response.errorBody()?.string()}")
        }
    }

}