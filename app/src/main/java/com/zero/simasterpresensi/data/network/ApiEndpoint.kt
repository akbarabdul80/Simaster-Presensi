package com.zero.simasterpresensi.data.network

import com.zero.simasterpresensi.converter.Json
import com.zero.simasterpresensi.data.db.Sessions
import com.zero.simasterpresensi.data.model.commit_device.ResponseCommitDevice
import com.zero.simasterpresensi.data.model.menu.ResponseMenuItem
import com.zero.simasterpresensi.data.model.presensi_kkn.ResponsePresensiKKN
import com.zero.simasterpresensi.data.model.scan_qr.ResponseScanQr
import com.zero.simasterpresensi.data.model.token.ResponseToken
import com.zero.simasterpresensi.data.model.user.DataUser
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.utils.getDate
import io.reactivex.Single
import org.jsoup.nodes.Document
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
    ): Single<ResponseScanQr>

    /**
     * Request Token QR
     */
    @Headers("UGMFWSERVICE: 1")
            @GET("https://simaster.ugm.ac.id/services/simaster/ongoing")
    fun getPagesKKN(
        @Query("sesId") sesId: String = App.sessions.getData(Sessions.sesId),
        @Query("groupMenu") groupMenu: String = App.sessions.getData(Sessions.groupMenu),
        @Query("menu") menu: String = "346"
    ): Single<String>


    /**
     * Request Token QR
     */
    @Headers("UGMFWSERVICE: 1")
    @GET("https://simaster.ugm.ac.id/services/simaster/get_menu")
    fun getMenu(
        @Query("sesId") sesId: String = App.sessions.getData(Sessions.sesId),
        @Query("groupMenu") groupMenu: String = App.sessions.getData(Sessions.groupMenu),
    ): Single<String>


    /**
     * Presensi KKN
     */
    @FormUrlEncoded
    @Headers("UGMFWSERVICE: 1")
    @POST("kkn/presensi/add")
    fun presensiKKN(
        @Field("simasterUGM_token") simasterUGM_token: String,
        @Field("latitude") latitudeGps: String,
        @Field("longtitude") longitudeGps: String,
        @Field("tanggalPresensi") tanggalPresensi: String = getDate(),
        @Field("agreement") agreement: String = "1"
    ): Single<String>
}
