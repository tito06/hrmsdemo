package com.prabal.hrmsattendance.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "attendance")
data class Attendance(

    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    val token: String,
    val date:String,
    val time:String,
)
