package com.zero.simasterpresensi.data.network

import com.zero.simasterpresensi.data.model.commit_device.ResponseCommitDevice
import com.zero.simasterpresensi.data.model.token.ResponseToken
import com.zero.simasterpresensi.data.model.user.DataUser
import io.reactivex.Single
import retrofit2.http.*

interface ApiEndpoint {

    /**
     * Login User
     */
    @FormUrlEncoded
    @POST("services/simaster/service_login")
    fun login(
        @Field("aId") aid: String,
        @Field("username") username: String,
        @Field("password") password: String,
    ): Single<DataUser>

    /**
     * Commit Device
     */
    @FormUrlEncoded
    @POST("services/simaster/commit_device")
    fun commitDevice(
        @Field("sesId") aid: String,
    ): Single<ResponseCommitDevice>

    /**
     * Request Token QR
     */
    @Headers("UGMFWSERVICE: 1")
    @GET("services/presensiqr/request_token")
    fun requestToken(
        @Header("Authorization") auth: String
    ): Single<ResponseToken>

    /**
     * Scan QR
     */
    @FormUrlEncoded
    @Headers("UGMFWSERVICE: 1")
    @POST("services/presensiqr/doscanent")
    fun scanQr(
        @Header("Authorization") auth: String,
        @Field("simasterUGM_token") simasterUGM_token: String,
        @Field("device") device: String,
        @Field("group") group: String,
        @Field("latitudeGps") latitudeGps: String,
        @Field("longitudeGps") longitudeGps: String,
        @Field("code") code: String,
    ): Single<ResponseCommitDevice>
}
