package com.zero.simasterpresensi.data.network

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException


class AddCookiesInterceptor(val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        for (cookie in PreferenceManager.getDefaultSharedPreferences(context)
            .getStringSet("PREF_COOKIES", HashSet())!!) {
            builder.addHeader("Cookie", cookie)
            Log.v(
                "OkHttp",
                "Adding Header: $cookie"
            ) // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }
        return chain.proceed(builder.build())
    }
}