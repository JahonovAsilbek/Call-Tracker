package uz.asaxiy.calltracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.asaxiy.calltracker.domain.dto.Call

@Database(entities = [Call::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): CallDao

    companion object {

        @Volatile
        private var database: AppDatabase? = null

        fun init(context: Context) {
            if (database == null) {
                database = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "call.db").fallbackToDestructiveMigration().build()
            }
        }

        fun getDatabase(): AppDatabase = database!!
    }
}