package com.zero.simasterpresensi.data.model.menu


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ResponseMenuItem(
    @SerializedName("css_clip")
    val cssClip: String,
    @SerializedName("identifier")
    val identifier: String,
    @SerializedName("menu")
    val menu: String,
    @SerializedName("module")
    val module: String,
    @SerializedName("parent_id")
    val parentId: String,
    @SerializedName("sequence")
    val sequence: String
) : Parcelable