package com.zero.simasterpresensi.root

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.zero.simasterpresensi.data.db.Sessions
import com.zero.simasterpresensi.di.module.networkModule
import com.zero.simasterpresensi.di.module.viewModelModule
import com.zero.simasterpresensi.service.KknPresensiService
import com.zero.simasterpresensi.service.hapler.WorkerKKNPresensi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.Calendar
import java.util.concurrent.TimeUnit


@SuppressLint("StaticFieldLeak")
class App : Application() {
    companion object {
        const val TAG_SCANQR = 1001
        lateinit var sessions: Sessions
    }

    override fun onCreate() {
        super.onCreate()
        sessions = Sessions(this)
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    networkModule,
                    viewModelModule
                )
            )
        }

        createNotificationChannel()

        val intent = Intent(this, KknPresensiService::class.java)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        } else {
//            startService(intent)
//        }

//        startService(intent)

//        scheduleBroadcastWorker()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                KknPresensiService.CHANNEL_ID,
                "KKN Presensi Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun scheduleApiService() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 12) // 11 pm
        calendar.set(Calendar.MINUTE, 34)
        calendar.set(Calendar.SECOND, 0)
        val intent = Intent(this, KknPresensiService::class.java)
        val pendingIntent =
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }
//
//    private fun scheduleBroadcastWorker() {
//        val uniqueWorkName = "kkn_presensi_periodic_work"
//        val calendar = Calendar.getInstance()
//        calendar[Calendar.HOUR_OF_DAY] = 13 // 11 pm
//        calendar[Calendar.MINUTE] = 24
//        calendar[Calendar.SECOND] = 0
//        var initialDelay = calendar.timeInMillis - System.currentTimeMillis()
//        if (initialDelay < 0) {
//            // If the current time is after 11 pm, schedule for the next day
//            initialDelay += TimeUnit.DAYS.toMillis(1)
//        }
//        val constraints: Constraints = Constraints.Builder()
//            .setRequiresCharging(false) // You can add more constraints if needed
//            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
//            .build()
//
//        val workRequest = PeriodicWorkRequest.Builder(
//            MyWorker::class.java,
//            1,  // Repeat interval, set to 1 day
//            TimeUnit.DAYS
//        )
//            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
//            .setConstraints(constraints)
//            .addTag(uniqueWorkName)
//            .build()
//        WorkManager.getInstance(this)
//            .beginUniqueWork(uniqueWorkName, ExistingWorkPolicy.REPLACE, workRequest)
//            .enqueue()
//
//        WorkManager.getInstance(baseContext).cancelAllWorkByTag(uniqueWorkName)
//
//        val notificationWork = OneTimeWorkRequest.Builder(MyWorker::class.java)
//            .addTag(uniqueWorkName)
//            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//            .setInputData(data)
//            .build()
//
//        val instanceWorkManager = WorkManager.getInstance(requireContext())
//        instanceWorkManager.beginUniqueWork(
//            NOTIFICATION_WORK,
//            ExistingWorkPolicy.REPLACE, notificationWork
//        ).enqueue()
//    }

    private fun schedulePeriodicWorkRequest(hour: Int, minute: Int) {
        // Set the time for the request
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = hour
        cal[Calendar.MINUTE] = minute
        val currentTimeMillis = System.currentTimeMillis()
        var selectedTimeMillis = cal.timeInMillis

        // If the selected time is in the past, schedule it for the next day
        if (selectedTimeMillis <= currentTimeMillis) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
            selectedTimeMillis = cal.timeInMillis
        }
        val timeDiff = selectedTimeMillis - currentTimeMillis

        // Schedule the PeriodicWorkRequest with the calculated time difference
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            WorkerKKNPresensi::class.java, 1, TimeUnit.DAYS
        )
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }


}


