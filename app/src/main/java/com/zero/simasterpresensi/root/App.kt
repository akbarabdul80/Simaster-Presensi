package com.zero.simasterpresensi.root

import android.annotation.SuppressLint
import android.app.Application
import com.zero.simasterpresensi.data.db.Sessions
import com.zero.simasterpresensi.di.module.networkModule
import com.zero.simasterpresensi.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@SuppressLint("StaticFieldLeak")
class App : Application() {
    companion object {
        const val TAG_SCANQR = 1001
        lateinit var sessions: Sessions
    }

    override fun onCreate() {
        super.onCreate()
        sessions = Sessions(this)
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    networkModule,
                    viewModelModule
                )
            )
        }
    }
}