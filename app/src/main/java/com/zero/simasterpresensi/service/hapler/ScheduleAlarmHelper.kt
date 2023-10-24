package com.zero.simasterpresensi.service.hapler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.zero.simasterpresensi.service.KknPresensiService
import java.util.Calendar

object ScheduleAlarmHelper {

    private const val ALARM_REQUEST_CODE = 1001 // A unique code to identify the alarm PendingIntent

    fun scheduleAlarm(context: Context) {
        // Create a Calendar instance for 01:00 AM
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // If the scheduled time is in the past, set it for the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Create an Intent for the BroadcastReceiver
        val intent = Intent(context, KknPresensiService::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Get the AlarmManager service
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the alarm to trigger at 01:00 AM every day
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

//    fun cancelAlarm(context: Context) {
//        val intent = Intent(context, MyAlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            ALARM_REQUEST_CODE,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        // Get the AlarmManager service and cancel the alarm
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.cancel(pendingIntent)
//    }
}
