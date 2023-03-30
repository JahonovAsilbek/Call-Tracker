package uz.asaxiy.calltracker.domain.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
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
