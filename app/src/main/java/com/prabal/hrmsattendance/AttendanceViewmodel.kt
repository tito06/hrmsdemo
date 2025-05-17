package com.prabal.hrmsattendance

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prabal.hrmsattendance.db.Attendance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AttendanceViewModel(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _punchStatus = MutableStateFlow<String?>(null)
    val punchStatus: StateFlow<String?> = _punchStatus

    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList: StateFlow<List<Attendance>> = _attendanceList



    private val _allAttendance = MutableStateFlow<List<Attendance>>(emptyList())
    val allAttendance: StateFlow<List<Attendance>> = _allAttendance

    private val dummyToken = "dummy_token_123"

    @RequiresApi(Build.VERSION_CODES.O)
    fun punchIn() {
        viewModelScope.launch {
            val success = repository.punchIn(dummyToken)
            if (success) {
                _punchStatus.value = "Punch-in successful!"
                loadAttendance()
            } else {
                _punchStatus.value = "Punch-in only allowed between 9 AM and 11 AM."
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAttendance() {

        val dummyToken = "dummy_token_123"
        viewModelScope.launch {
            val all = repository.getAll()
            val today = LocalDate.now()
            val monday = today.with(java.time.DayOfWeek.MONDAY)
            val sunday = monday.plusDays(6)


           _attendanceList.value = all.filter {
                val date = java.time.LocalDate.parse(it.date.substring(0,10))
                !date.isBefore(monday) && !date.isAfter(sunday)
            }


        }
    }


    fun loadAllAttendance() {

        viewModelScope.launch {
            _allAttendance.value = repository.getAll()

        }
    }
}
