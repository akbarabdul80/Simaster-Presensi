package com.zero.simasterpresensi.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Start your service when the boot is completed.
            val serviceIntent = Intent(context, KknPresensiService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            Toast.makeText(context, "Service started after boot completion", Toast.LENGTH_SHORT).show()
            Log.d("BootCompleteReceiver", "Service started after boot completion.")
        }
    }
}