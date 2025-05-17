package com.prabal.hrmsattendance.db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Database(entities = [Attendance::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_db"
                )
                    .addCallback(RoomCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // dummy data
    private class RoomCallback : Callback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    val dao = database.attendanceDao()
                    val dummyToken = "dummy_token_123"
                    val formatter = DateTimeFormatter.ofPattern("HH:mm a")
                    val now = java.time.LocalTime.now()

                    for (i in 1..14) {
                        val date = LocalDate.now().minusDays(i.toLong()).toString()
                        val time = now.minusMinutes((10..120).random().toLong()).format(formatter)
                        val attendance = Attendance(
                            token = dummyToken,
                            date = date,
                            time = time
                        )
                        dao.insertAttendance(attendance)
                    }
                }
            }
        }
    }
}
