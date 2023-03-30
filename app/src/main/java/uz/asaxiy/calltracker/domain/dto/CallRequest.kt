package uz.asaxiy.calltracker.domain.dto

import com.google.gson.annotations.SerializedName

data class CallRequest(
    @SerializedName("user_id")
    val userID: String,
    val calls: List<Call>
)
