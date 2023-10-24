package com.zero.simasterpresensi.ui.kkn

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oratakashi.viewbinding.core.binding.livedata.liveData
import com.zero.simasterpresensi.data.network.ApiEndpoint
import com.zero.simasterpresensi.data.state.SimpleState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class KKNViewModel(private val endpoint: ApiEndpoint) : ViewModel() {
    val stateMain: MutableLiveData<SimpleState> by liveData()
    val stateKKN: MutableLiveData<SimpleState> by liveData()

    fun getPagesKKN() {
        endpoint.getPagesKKN()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map<SimpleState>(SimpleState::Result)
            .onErrorReturn(SimpleState::Error)
            .toFlowable()
            .startWith(SimpleState.Loading)
            .subscribe(stateMain::postValue)
            .let { return@let CompositeDisposable::add }
    }

    fun presensiKKN(
        simasterUGM_token: String,
        latitude: String,
        longitude: String,
    ) {
        endpoint.presensiKKN(simasterUGM_token, latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map<SimpleState>(SimpleState::Result)
            .onErrorReturn(SimpleState::Error)
            .toFlowable()
            .startWith(SimpleState.Loading)
            .subscribe(stateKKN::postValue)
            .let { return@let CompositeDisposable::add }
    }

}