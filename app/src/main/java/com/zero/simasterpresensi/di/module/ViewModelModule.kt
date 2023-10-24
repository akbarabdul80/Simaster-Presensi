package com.zero.simasterpresensi.di.module

import com.zero.simasterpresensi.ui.kkn.KKNViewModel
import com.zero.simasterpresensi.ui.login.LoginViewModel
import com.zero.simasterpresensi.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { KKNViewModel(get(named("service_html"))) }
}