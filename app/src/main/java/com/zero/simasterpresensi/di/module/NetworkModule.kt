package com.zero.simasterpresensi.di.module

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.zero.simasterpresensi.BuildConfig
import com.zero.simasterpresensi.data.network.AddCookiesInterceptor
import com.zero.simasterpresensi.data.network.ApiEndpoint
import com.zero.simasterpresensi.data.network.HtmlConverterFactory
import com.zero.simasterpresensi.data.network.ReceivedCookiesInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val networkModule = module {
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get(), BuildConfig.BASE_URL) }
    single { provideApiService(get()) }
    single(named("html")) { provideRetrofitHtml(get(), BuildConfig.BASE_URL) }
    single(named("service_html")) { provideApiServiceHtml(get(named("html"))) }
}

private fun provideOkHttpClient(context: Context): OkHttpClient {
    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ReceivedCookiesInterceptor(context))
            .addInterceptor(AddCookiesInterceptor(context))
            .build()
    } else return OkHttpClient
        .Builder()
        .addInterceptor(ReceivedCookiesInterceptor(context))
        .addInterceptor(AddCookiesInterceptor(context))
        .build()
}

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

private fun provideRetrofitHtml(
    okHttpClient: OkHttpClient,
    BASE_URL: String
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideApiService(retrofit: Retrofit): ApiEndpoint =
    retrofit.create(ApiEndpoint::class.java)

private fun provideApiServiceHtml(retrofit: Retrofit): ApiEndpoint =
    retrofit.create(ApiEndpoint::class.java)
