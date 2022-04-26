package com.zero.simasterpresensi.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oratakashi.viewbinding.core.binding.livedata.liveData
import com.zero.simasterpresensi.data.network.ApiEndpoint
import com.zero.simasterpresensi.data.state.SimpleState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val endpoint: ApiEndpoint) : ViewModel() {
    val stateMain: MutableLiveData<SimpleState> by liveData()

    fun requestToken(auth: String) {
        endpoint.requestToken(auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map<SimpleState>(SimpleState::Result)
            .onErrorReturn(SimpleState::Error)
            .toFlowable()
            .startWith(SimpleState.Loading)
            .subscribe(stateMain::postValue)
            .let { return@let CompositeDisposable::add }
    }

    fun scanQr(
        auth: String,
        simasterUGM_token: String,
        device: String,
        group: String,
        latitudeGps: String,
        longitudeGps: String,
        code: String,
    ) {
        endpoint.scanQr(auth, simasterUGM_token, device, group, latitudeGps, longitudeGps, code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map<SimpleState>(SimpleState::Result)
            .onErrorReturn(SimpleState::Error)
            .toFlowable()
            .startWith(SimpleState.Loading)
            .subscribe(stateMain::postValue)
            .let { return@let CompositeDisposable::add }
    }

}