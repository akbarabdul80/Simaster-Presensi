package com.zero.simasterpresensi.data.network

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.zero.simasterpresensi.data.db.Sessions
import com.zero.simasterpresensi.root.App
import okhttp3.Cookie
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import retrofit2.adapter.rxjava2.Result.response


class ReceivedCookiesInterceptor(val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
//        Log.e("TAG", "intercept: ${originalResponse.headers}")
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {

            val sessions = Sessions(context)

            val cookies: HashSet<String> = HashSet()
            for (header in originalResponse.headers("Set-Cookie")) {
                Log.e("TAG", "intercept: $header")

                val tmpCookie = header.split("; ").toTypedArray()
                for (cookie in tmpCookie) {
                    Log.e("TAG", "tmpCookie: $cookie")
                    if (cookie.contains("simasterUGM_cookie")) {
                        sessions.putData(
                            Sessions.simasterUGM_cookie, cookie.split("=").toTypedArray()[1]
                        )
                        Log.e(
                            "TAG",
                            "save simasterUGM_cookie: ${
                                cookie.split("=").toTypedArray()[1]
                            }"
                        )
                    }
                    if (cookie.contains("simaster-ugm_sess")) {
                        sessions.putData(
                            Sessions.simaster_ugm_sess, cookie.split("=").toTypedArray()[1]
                        )
                        Log.e(
                            "TAG",
                            "save simaster_ugm_sess: ${
                                cookie.split("=").toTypedArray()[1]
                            }"
                        )
                    }

                }
                cookies.add(header)
            }
            val memes = PreferenceManager.getDefaultSharedPreferences(context).edit()
            memes.putStringSet("PREF_COOKIES", cookies).apply()
            memes.apply()
        }
        return originalResponse
    }

    fun getCookieList(): HashSet<String> {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getStringSet("PREF_COOKIES", HashSet()) as HashSet<String>
    }

}