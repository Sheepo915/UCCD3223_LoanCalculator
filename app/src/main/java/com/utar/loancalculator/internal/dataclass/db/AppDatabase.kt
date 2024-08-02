package com.utar.loancalculator.internal.dataclass.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utar.loancalculator.internal.dao.SavedCalculationDao

@Database(
    entities = [SavedCalculation::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedCalculationDao(): SavedCalculationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDatabase::class.java,
                    name = "data"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
