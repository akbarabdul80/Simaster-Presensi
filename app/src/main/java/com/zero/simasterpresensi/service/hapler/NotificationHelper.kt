package com.zero.simasterpresensi.service.hapler

import android.app.Notification
import android.app.Notification.DEFAULT_SOUND
import android.app.Notification.DEFAULT_VIBRATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.zero.simasterpresensi.R
import com.zero.simasterpresensi.service.KknPresensiService
import com.zero.simasterpresensi.ui.main.MainActivity


class NotificationHelper(private val context: Context, private val service: Service) {


    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotification(title: String?, message: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "KKN Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, KknPresensiService.CHANNEL_ID).setContentTitle(title)
                .setContentText(message).setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
                .setOngoing(true) // Make the notification ongoing, preventing dismissal by the user
                .build()
        } else {
            Notification.Builder(context).setContentTitle(title).setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
                .setOngoing(true) // Make the notification ongoing, preventing dismissal by the user
                .build()
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
        service.startForeground(NOTIFICATION_ID, notification)
    }

    fun updateNotification(apiCallSuccess: Boolean) {
        val title = "API Service"
        val message = if (apiCallSuccess) "API call successful." else "API call failed."

        val notificationIntent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val updatedNotification: Notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, KknPresensiService.CHANNEL_ID).setContentTitle(title)
                    .setContentText(message).setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
                    .setOngoing(true) // Make the notification ongoing, preventing dismissal by the user
                    .build()
            } else {
                Notification.Builder(context).setContentTitle(title).setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setOngoing(true) // Make the notification ongoing, preventing dismissal by the user
                    .build()
            }

        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
        service.startForeground(NOTIFICATION_ID, updatedNotification)
    }

    fun updateNotification(title: String, message: String) {

        val notificationIntent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )


        val updatedNotification: Notification =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(context, KknPresensiService.CHANNEL_ID).setContentTitle(title)
                    .setContentText(message).setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
                    .setOngoing(true) // Make the notification ongoing, preventing dismissal by the user
                    .build()
            } else {
                Notification.Builder(context).setContentTitle(title).setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
                    .setOngoing(true) // Make the notification ongoing, preventing dismissal by the user
                    .build()
            }

        notificationManager.notify(NOTIFICATION_ID, updatedNotification);
        service.startForeground(NOTIFICATION_ID, updatedNotification)
    }

    fun cancelNotification() {
        Log.e("TAG", "cancelNotification: ")
        notificationManager.cancel(NOTIFICATION_ID)
        service.stopForeground(true)
    }

    companion object {
        const val CHANNEL_ID = "KKNPresensiServiceChannel"
        const val NOTIFICATION_ID = 10121
    }
}
