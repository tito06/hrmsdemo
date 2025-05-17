package com.prabal.hrmsattendance

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabal.hrmsattendance.db.Attendance
import kotlinx.coroutines.flow.collectLatest
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(modifier: Modifier,viewModel: AttendanceViewModel) {


    val attendanceList by viewModel.attendanceList.collectAsState()
    val punchStatus by viewModel.punchStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAttendance()
        viewModel.loadAllAttendance()
    }

    Column {
        CustomAppBar()
        Text(
            text = "Week's Overview",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp, 16.dp, 0.dp, 8.dp)
        )
        WeeklyCalendarView(attendanceList)
        Text(
            text = "Today",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp, 16.dp, 0.dp, 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        MarkAttendance(viewModel)
        if (!punchStatus.isNullOrEmpty()) {
            Text(
                text = punchStatus!!,
                modifier = Modifier.padding(16.dp),
                color = if (punchStatus!!.contains("successful")) Color.Green else Color.Red
            )
        }
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp, 16.dp, 0.dp, 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        RecentActivity()
    }
}

@Composable
fun CustomAppBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        color = Color(0xFF5C7CEF),
        tonalElevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Good Morning, Mr Prabal,",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp, top = 60.dp)
                )
                Text(
                    text = "Welcome back...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyCalendarView(attendanceList: List<Attendance>) {
    val currentDate = LocalDate.now()
    val startWeek = currentDate.with(DayOfWeek.MONDAY)
    val weekDates = (0..6).map { startWeek.plusDays(it.toLong()) }

    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val punchInDates = attendanceList.map { it.date }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDates.forEach { date ->
            val isToday = date == currentDate
            val isPunched = punchInDates.contains(date.format(formatter))

            val bgColor = when {
                isToday -> Color.Yellow
                isPunched -> Color.Green
                else -> Color(0xFFB1D2FB)
            }

            Card(
                modifier = Modifier
                    .height(80.dp)
                    .width(50.dp),
                shape = RoundedCornerShape(21.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = date.dayOfWeek.name.take(3),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(text = date.dayOfMonth.toString())
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MarkAttendance(viewModel: AttendanceViewModel) {
    var expand by remember { mutableStateOf(false) }

    val animatedRotationY by animateFloatAsState(
        targetValue = if (expand) 180f else 0f,
        label = "",
        animationSpec = tween(durationMillis = 600)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB1D2FB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mark Your Attendance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
                    .graphicsLayer {
                        rotationY = animatedRotationY
                        cameraDistance = 12f * density
                    }
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                if (animatedRotationY <= 90f) {
                    ElevatedButton(
                        onClick = {
                            viewModel.punchIn()
                            expand = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C7CEF)),
                        modifier = Modifier.alpha(1f)
                    ) {
                        Text("Punch In")
                    }
                } else {
                    ElevatedButton(
                        onClick = {
                            // Optional punch-out behavior
                            expand = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5C7C)),
                        modifier = Modifier
                            .graphicsLayer { rotationY = 180f }
                            .alpha(1f)
                    ) {
                        Text("Punch Out")
                    }
                }
            }
        }
    }
}


@Composable
fun RecentActivity() {

    val viewModel: AttendanceViewModel = viewModel()
    val allAttendance by viewModel.allAttendance.collectAsState()

    LazyColumn {
        items(allAttendance.size) {

            Card(
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(21.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB1D2FB))
            ) {
               Column(modifier = Modifier.fillMaxSize().padding(8.dp,0.dp),
                   verticalArrangement = Arrangement.Center,
                   horizontalAlignment = Alignment.Start) {



                       Text("DATE :  " + allAttendance[it].date)

                        Text("PUNCH TIME :  " + allAttendance[it].time)

               }
            }
        }
    }

}
