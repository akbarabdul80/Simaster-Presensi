package com.zero.simasterpresensi.data.model.user

import com.google.gson.annotations.SerializedName

data class ResponseUser(
    @SerializedName("sukses") val success: Int?,
    @SerializedName("pesan") val message: String?,
    @SerializedName("received") val data: List<DataUser>?
)
