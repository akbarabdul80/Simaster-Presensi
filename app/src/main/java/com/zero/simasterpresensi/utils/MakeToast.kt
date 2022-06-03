package com.zero.simasterpresensi.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.zero.simasterpresensi.BuildConfig
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.ui.login.LoginActivity
import retrofit2.HttpException
import java.net.SocketTimeoutException

object MakeToast {
    fun toastThrowable(context: Context, error: Throwable) {
        if (error is HttpException) {
            when (error.code()) {
                400 -> {
                    Toast.makeText(context, "Silahkan cek koneksi anda", Toast.LENGTH_LONG).show()
                }
                404 -> Toast.makeText(
                    context,
                    "Url yang diminta tidak ditemukan",
                    Toast.LENGTH_LONG
                ).show()

                500 -> Toast.makeText(context, "Server sedang gangguan", Toast.LENGTH_LONG).show()

                403 -> {
                    Toast.makeText(context, "Anda sudah login didevice lain!", Toast.LENGTH_LONG)
                        .show()
                    App.sessions.logout()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as Activity).finish()
                }

                else -> if (BuildConfig.DEBUG)
                    Toast.makeText(
                        context, "Sedang Galat", Toast.LENGTH_LONG
                    ).show()
            }
        } else if (error is SocketTimeoutException) {
            Toast.makeText(context, "Silahkan cek koneksi anda", Toast.LENGTH_LONG).show()
        } else {
            if (BuildConfig.DEBUG)
                Toast.makeText(
                    context, "Gagal memuat data : " +
                            "${error.printStackTrace()}", Toast.LENGTH_LONG
                ).show()
        }
    }
}