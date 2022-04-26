package com.zero.simasterpresensi.data.state

sealed class SimpleState {
    object Loading : SimpleState()
    data class Result<T>(val data: T) : SimpleState()
    data class Error(val error: Throwable) : SimpleState()
}
