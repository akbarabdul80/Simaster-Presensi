package com.zero.simasterpresensi.di.module

import com.zero.simasterpresensi.ui.login.LoginViewModel
import com.zero.simasterpresensi.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel(get()) }
}