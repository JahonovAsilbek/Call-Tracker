package uz.asaxiy.calltracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.asaxiy.calltracker.domain.dto.Call

@Dao
interface CallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calls: List<Call>)

    @Query("select * from call")
    fun allCalls(): Flow<List<Call>>

    @Query("select count(date) from call")
    fun callCount(): Int

    @Query("delete from call")
    fun clearCalls()
}