package uz.asaxiy.calltracker.data.locale.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.asaxiy.calltracker.data.locale.entity.Call

@Dao
interface CallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: Call)

    @Delete
    suspend fun deleteCall(call: Call)

    @Query("SELECT * FROM call")
    fun getAllCalls(): Flow<List<Call>>

    @Query("delete from call")
    suspend fun deleteAllCalls()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(calls: ArrayList<Call>)
}