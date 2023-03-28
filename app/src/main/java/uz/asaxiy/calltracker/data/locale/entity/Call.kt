package uz.asaxiy.calltracker.data.locale.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Call(
    @SerializedName("date")
    @PrimaryKey
    val date: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("type")
    val type: Int
)
