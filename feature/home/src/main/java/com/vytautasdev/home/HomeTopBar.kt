package com.vytautasdev.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onMenuClicked: () -> Unit,
    dateIsSelected: Boolean,
    onDateSelected: (ZonedDateTime) -> Unit,
    onDateReset: () -> Unit
) {
    val dateDialog = rememberSheetState()
    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }
    TopAppBar(scrollBehavior = scrollBehavior, title = {
        Text(text = "My Diary", fontSize = 16.sp)
    }, navigationIcon = {
        IconButton(onClick = onMenuClicked) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Hamburger Menu Icon",
                modifier = Modifier.size(26.dp)

            )
        }
    }, actions = {
        if (dateIsSelected) {
            IconButton(onClick = onDateReset) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(26.dp)
                )
            }
        } else {
            IconButton(onClick = { dateDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar Icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    })

    CalendarDialog(
        state = dateDialog, selection = CalendarSelection.Date { localDate ->
            pickedDate = localDate
            onDateSelected(
                ZonedDateTime.of(
                    pickedDate, LocalTime.now(), ZoneId.systemDefault()
                )
            )
        }, config = CalendarConfig(monthSelection = true, yearSelection = true)
    )
}

