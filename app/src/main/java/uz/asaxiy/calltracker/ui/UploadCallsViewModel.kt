package uz.asaxiy.calltracker.ui

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import uz.asaxiy.calltracker.data.locale.entity.Call
import uz.asaxiy.calltracker.data.remote.ApiClient
import uz.asaxiy.calltracker.util.Resource
import uz.asaxiy.calltracker.util.formatDate
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

    fun getCallHistory(context: Context, uploadToServer: Boolean = false) {
        val callLogs = ArrayList<Call>()

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
                        val callDuration: Int? = managedCursor.getString(duration).toIntOrNull()
//                        var dir = ""
                        val dircode = callType.toInt()
//                        when (dircode) {
//                            CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
//                            CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
//                            CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
//                        }

                        val call = Call(
                            date = callDayTime.time.formatDate(),
                            number = phNumber.formatPhone(),
                            duration = callDuration ?: 0,
                            type = dircode
                        )

                        if (phNumber.isNotEmpty())
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
            val response = ApiClient.apiService.postCall(list = calls)
            if (response.isSuccessful) {
                _uploadState.emit(Resource.Success(response.body()))
            } else {
                _uploadState.emit(Resource.Error(response.errorBody().toString()))
            }
        }
    }

}