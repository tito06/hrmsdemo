package com.prabal.hrmsattendance

import android.os.Build
import androidx.annotation.RequiresApi
import com.prabal.hrmsattendance.db.Attendance
import com.prabal.hrmsattendance.db.AttendanceDao
import java.time.LocalTime


class AttendanceRepository(private  val dao: AttendanceDao) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun punchIn(token: String):Boolean{

        val now = LocalTime.now()
        val valid = now.isAfter(java.time.LocalTime.of(9,0)) &&
                now.isBefore(java.time.LocalTime.of(11,0))

        if(!valid) return false

        val currentDate = java.time.LocalDate.now().toString()
        val currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm a"))

        val attendance = Attendance(token = token, date =  currentDate, time =  currentTime)
        dao.insertAttendance(attendance)
        return true
    }

    suspend fun getAll() = dao.getAllAttendance()
}