package com.zero.simasterpresensi.di.module

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.zero.simasterpresensi.BuildConfig
import com.zero.simasterpresensi.data.network.AddCookiesInterceptor
import com.zero.simasterpresensi.data.network.ApiEndpoint
import com.zero.simasterpresensi.data.network.ReceivedCookiesInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get(), BuildConfig.BASE_URL) }
    single { provideApiService(get()) }
}

private fun provideOkHttpClient(context: Context) = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .addInterceptor(ChuckerInterceptor(context))
        .addInterceptor(loggingInterceptor)
        .build()
} else OkHttpClient
    .Builder()
    .addInterceptor(ReceivedCookiesInterceptor(context))
    .addInterceptor(AddCookiesInterceptor(context))
    .build()

private fun provideRetrofit(
    okHttpClient: OkHttpClient,
    BASE_URL: String
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideApiService(retrofit: Retrofit): ApiEndpoint =
    retrofit.create(ApiEndpoint::class.java)
