package com.zero.simasterpresensi.data.model.token

import com.google.gson.annotations.SerializedName

data class ResponseToken(
    @SerializedName("message") val message: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("heading") val heading: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("value") val value: String?,
)
