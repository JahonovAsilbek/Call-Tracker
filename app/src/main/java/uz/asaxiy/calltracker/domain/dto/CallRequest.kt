package uz.asaxiy.calltracker.domain.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CallRequest(
    @SerializedName("user_id")
    val userID: String,
    val calls: List<Call>
)
