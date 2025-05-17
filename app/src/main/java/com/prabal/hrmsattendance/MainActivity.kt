package com.prabal.hrmsattendance

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Scaffold

import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabal.hrmsattendance.db.AppDatabase

import com.prabal.hrmsattendance.ui.theme.HrmsAttendanceTheme



class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val db = AppDatabase.getDatabase(this)
        val repository = AttendanceRepository(db.attendanceDao())

        val factory = AttendanceViewmodelFactory(repository)
        setContent {
            HrmsAttendanceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val attendanceViewModel: AttendanceViewModel = viewModel(factory = factory)

                    DashboardScreen(modifier = Modifier.padding(innerPadding) ,viewModel = attendanceViewModel)
                }
            }
        }
    }
}















