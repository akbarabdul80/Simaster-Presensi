package com.zero.simasterpresensi.data.model.commit_device

import com.google.gson.annotations.SerializedName

data class ResponseCommitDevice(
    @SerializedName("message") val message: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("heading") val heading: String?
)
