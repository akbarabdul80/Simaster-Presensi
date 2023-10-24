package com.zero.simasterpresensi.utils

import java.util.Calendar


// get current date 25-06-2023
fun getDate(): String {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    return "$day-${month + 1}-$year"
}