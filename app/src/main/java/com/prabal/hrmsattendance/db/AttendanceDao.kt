package com.prabal.hrmsattendance.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance ORDER BY date DESC")
    suspend fun getAllAttendance(): List<Attendance>

}