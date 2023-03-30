package uz.asaxiy.calltracker.domain.dto

import com.google.gson.annotations.SerializedName

data class Call(
    @SerializedName("date")
    val date: Long,
    @SerializedName("number")
    val number: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("type")
    val type: Int
)
