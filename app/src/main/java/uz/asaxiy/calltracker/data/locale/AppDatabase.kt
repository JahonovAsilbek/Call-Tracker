package uz.asaxiy.calltracker.data.locale

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.asaxiy.calltracker.data.locale.dao.CallDao
import uz.asaxiy.calltracker.data.locale.entity.Call

@Database(entities = [Call::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun callDao(): CallDao

    companion object {
        const val DATABASE_NAME = "call_tracker_db"

        private var instance: AppDatabase? = null

        fun init(application: Application) {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
        }

        fun getInstance(): AppDatabase {
            return instance!!
        }
    }
}