package com.zero.simasterpresensi.utils

import android.content.Context
import android.widget.Toast
import com.zero.simasterpresensi.BuildConfig
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

                403 -> Toast.makeText(context, "Sedang terjadi galat", Toast.LENGTH_LONG).show()

                else -> if (BuildConfig.DEBUG)
                    Toast.makeText(
                        context, "Gagal memuat data : " +
                                "${error.message()} ${error.code()}", Toast.LENGTH_LONG
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