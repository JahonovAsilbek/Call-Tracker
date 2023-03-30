package uz.asaxiy.calltracker.ui

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import uz.asaxiy.calltracker.domain.dto.Call
import uz.asaxiy.calltracker.data.remote.ApiClient
import uz.asaxiy.calltracker.domain.dto.CallRequest
import uz.asaxiy.calltracker.util.MyLocalStorage
import uz.asaxiy.calltracker.util.Resource
import uz.asaxiy.calltracker.util.formatPhone
import java.lang.Long
import java.util.*
import kotlin.Boolean
import kotlin.Exception
import kotlin.Int
import kotlin.String
import kotlin.toString

class UploadCallsViewModel : ViewModel() {

    private val _uploadState: MutableSharedFlow<Resource> = MutableSharedFlow()
    val uploadState get() = _uploadState

    private val _callHistory: MutableSharedFlow<Resource> = MutableSharedFlow()
    val callHistory get() = _callHistory

    fun getCallHistory(context: Context, uploadToServer: Boolean = true) {
        val callLogs = ArrayList<Call>()
        var historyTime = 0L //gaplashilgan vaqti

        viewModelScope.launch(Dispatchers.IO) {

            _uploadState.emit(Resource.Loading)
            _callHistory.emit(Resource.Loading)

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
                    while (managedCursor.moveToNext()) {
                        val phNumber: String = managedCursor.getString(number).formatPhone()
                        val callType: String = managedCursor.getString(type)
                        val callDate: String = managedCursor.getString(date)
                        val callDayTime = Date(Long.valueOf(callDate))
                        historyTime = callDayTime.time
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

                        if (phNumber.isNotEmpty() && (currentTime - callDayTime.time < 86400000))
                            callLogs.add(call)
                    }
                    managedCursor.close()

                    _callHistory.emit(Resource.Success(data = callLogs))
                    if (uploadToServer)
                        uploadCalls(callLogs)

                }
            } catch (e: Exception) {
                _callHistory.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun uploadCalls(calls: List<Call>) {
        viewModelScope.launch {
            val response = ApiClient.apiService.postCall(
                CallRequest(MyLocalStorage.userPhoneNumber ?: "", calls)
            )
            if (response.isSuccessful) {
                _uploadState.emit(Resource.Success(response.body()))
            } else {
                _uploadState.emit(Resource.Error(response.errorBody().toString()))
            }
        }
    }

}