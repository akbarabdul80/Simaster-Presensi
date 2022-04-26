package com.zero.simasterpresensi.data.model.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataUser(
    @SerializedName("sesId") val sesId: String,
    @SerializedName("namaLengkap") val namaLengkap: String?,
    @SerializedName("groupMenuNama") val groupMenuNama: String?,
    @SerializedName("userTipeNomor") val userTipeNomor: String?,
    @SerializedName("img") var img: String?,
    @SerializedName("groupMenu") val groupMenu: String?,
    @SerializedName("isLogin") val isLogin: Int?,
    @SerializedName("device") var device: String?,
) : Parcelable
