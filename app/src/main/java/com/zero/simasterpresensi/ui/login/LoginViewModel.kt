package com.zero.simasterpresensi.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oratakashi.viewbinding.core.binding.livedata.liveData
import com.zero.simasterpresensi.data.network.ApiEndpoint
import com.zero.simasterpresensi.data.state.SimpleState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val endpoint: ApiEndpoint) : ViewModel() {
    val stateLogin: MutableLiveData<SimpleState> by liveData()

    fun postLogin(aid: String, username: String, password: String) {
        endpoint.login(aid, username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map<SimpleState>(SimpleState::Result)
            .onErrorReturn(SimpleState::Error)
            .toFlowable()
            .startWith(SimpleState.Loading)
            .subscribe(stateLogin::postValue)
            .let { return@let CompositeDisposable::add }
    }

    fun commitDevice(sesId: String) {
        endpoint.commitDevice(sesId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map<SimpleState>(SimpleState::Result)
            .onErrorReturn(SimpleState::Error)
            .toFlowable()
            .startWith(SimpleState.Loading)
            .subscribe(stateLogin::postValue)
            .let { return@let CompositeDisposable::add }
    }
}