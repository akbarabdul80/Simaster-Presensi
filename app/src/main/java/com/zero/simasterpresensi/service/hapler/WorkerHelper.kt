package com.zero.simasterpresensi.service.hapler

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WorkerHelper(private val application: Application) : LifecycleObserver {
    companion object {
        private const val WORKER_TAG = "MyWorker"
        private const val WORKER_REPEAT_INTERVAL_DAYS = 1
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                fun scheduleWorker() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false) // You can add more constraints if needed
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23) // 11 pm
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        var initialDelay = calendar.timeInMillis - System.currentTimeMillis()
        if (initialDelay < 0) {
            // If the current time is after 11 pm, schedule for the next day
            initialDelay += TimeUnit.DAYS.toMillis(WORKER_REPEAT_INTERVAL_DAYS.toLong())
        }

        val workRequest = PeriodicWorkRequest.Builder(
            WorkerKKNPresensi::class.java,
            WORKER_REPEAT_INTERVAL_DAYS.toLong(),
            TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(WORKER_TAG)
            .build()

        WorkManager.getInstance(application).enqueue(workRequest)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelWorker() {
        WorkManager.getInstance(application).cancelAllWorkByTag(WORKER_TAG)
    }
}
