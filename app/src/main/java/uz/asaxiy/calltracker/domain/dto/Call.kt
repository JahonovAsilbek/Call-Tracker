package uz.asaxiy.calltracker.domain.dto

import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Keep
@Entity(tableName = "call")
data class Call(
    @SerializedName("date")
    @PrimaryKey
    val date: Long,
    @SerializedName("number")
    val number: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("type")
    val type: Int
)
