package com.zero.simasterpresensi.data.model.scan_qr

import com.google.gson.annotations.SerializedName

data class ResponseScanQr(
    @SerializedName("message") val message: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("heading") val heading: String?
)
