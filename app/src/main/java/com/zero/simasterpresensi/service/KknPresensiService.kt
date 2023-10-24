package com.zero.simasterpresensi.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.zero.simasterpresensi.data.db.Sessions
import com.zero.simasterpresensi.data.state.SimpleState
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.service.hapler.NotificationHelper
import com.zero.simasterpresensi.ui.kkn.KKNViewModel
import com.zero.simasterpresensi.utils.MakeToast
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class KknPresensiService : Service() {

    companion object {
        const val CHANNEL_ID = "KKNPresensiServiceChannel"
        const val ACTION_UPDATE_NOTIFICATION = "com.zero.simasterpresensi.UPDATE_NOTIFICATION"
        const val ACTION_PRESENSI_NOW = "com.zero.simasterpresensi.PRESENSI_NOW"
        const val ACTION_KILL_SERVICE = "com.zero.simasterpresensi.KILL_SERVICE"
    }

    private val viewModel: KKNViewModel by inject()

    private val longProcess by lazy {
        App.sessions.getData(Sessions.FAKE_LONG)
    }
    private val latProcess by lazy {
        App.sessions.getData(Sessions.FAKE_LAT)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    private val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(this, this)
    }

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != null) {
                when (intent.action) {
                    ACTION_UPDATE_NOTIFICATION -> {
                        val apiCallSuccess = intent.getBooleanExtra("api_call_success", false)
                        notificationHelper.updateNotification(apiCallSuccess)
                    }

                    ACTION_PRESENSI_NOW -> {
                        viewModel.getPagesKKN()
                    }

                    ACTION_KILL_SERVICE -> {
                        notificationHelper.cancelNotification()
                        stopForeground(true)
                        stopSelf()
                    }
                }
            }
        }
    }

    private fun observer() {
        viewModel.stateMain.observeForever { state ->
            state.let {
                when (it) {
                    is SimpleState.Loading -> {
                        notificationHelper.updateNotification(
                            "KKN PRESENSI SERVICE",
                            "Mengambil Cookie..."
                        )
                    }

                    is SimpleState.Result<*> -> {
                        viewModel.presensiKKN(
                            App.sessions.getData(Sessions.simasterUGM_cookie),
                            latProcess,
                            longProcess
                        )
                        notificationHelper.updateNotification(
                            "KKN PRESENSI SERVICE",
                            "Mengambil Cookie Berhasil"
                        )
                    }

                    is SimpleState.Error -> {
                        notificationHelper.updateNotification(
                            "KKN PRESENSI SERVICE", "Gagal Presensi, akan diulang 5 menit lagi"
                        )
                        MakeToast.toastThrowable(
                            this, it.error
                        )
                    }
                }
            }
        }

        viewModel.stateKKN.observeForever { state ->
            state.let {
                when (it) {
                    is SimpleState.Loading -> {
                        notificationHelper.updateNotification(
                            "KKN PRESENSI SERVICE",
                            "Melakukan Presensi..."
                        )
                    }

                    is SimpleState.Result<*> -> {

                        if (it.data is String) {
                            if (it.data.contentEquals("Mohon maaf, saudara sudah melakukan <b>presensi</b>")) {
                                Toast.makeText(baseContext, "Mohon maaf, saudara sudah melakukan <b>presensi</b>", Toast.LENGTH_SHORT).show()
                            }

                            Log.e("TAG", "observer: ${it.data}")

                        }
                        notificationHelper.updateNotification(
                            "KKN PRESENSI SERVICE",
                            "Presensi Berhasil ${getCurrentTimeString()}, besok akan presensi otomatis pukul 00:05"
                        )
                        Toast.makeText(
                            baseContext,
                            "Presensi Otomatis Berhasil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is SimpleState.Error -> {
                        notificationHelper.updateNotification(
                            "KKN PRESENSI SERVICE", "Gagal Presensi, akan diulang 5 menit lagi"
                        )
                        MakeToast.toastThrowable(
                            this, it.error
                        )
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(ACTION_UPDATE_NOTIFICATION)
        filter.addAction(ACTION_PRESENSI_NOW)
        filter.addAction(ACTION_KILL_SERVICE)
        registerReceiver(notificationReceiver, filter)
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, filter)
        observer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Show notification indicating the service is running
        notificationHelper.createNotification("KKN PRESENSI SERVICE", "Service is running.")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        unregisterReceiver(notificationReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver)
    }

    fun getCurrentTimeString(): String {
        // Get the current date and time
        val currentTime = Date()
        // Define the desired format
        val dateFormat = SimpleDateFormat("hh:mm a, EEEE, dd MMMM yyyy", Locale.getDefault())
        // Format the date and time to the desired format
        return dateFormat.format(currentTime)
    }
}
