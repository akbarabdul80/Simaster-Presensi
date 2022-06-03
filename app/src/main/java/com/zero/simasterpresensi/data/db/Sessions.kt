package com.zero.simasterpresensi.data.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.zero.simasterpresensi.BuildConfig
import com.zero.simasterpresensi.data.model.user.DataUser


@SuppressLint("CommitPrefEdits")
class Sessions(context: Context) {
    companion object {
        var PREF_NAME = BuildConfig.APPLICATION_ID + ".session"

        const val sesId: String = "sesId"
        const val namaLengkap: String = "namaLengkap"
        const val groupMenuNama: String = "groupMenuNama"
        const val userTipeNomor: String = "userTipeNomor"
        const val img: String = "img"
        const val groupMenu: String = "groupMenu"
    }

    var masterKey: MasterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    var pref: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var editor = pref.edit()

    var context: Context? = null

    init {
        this.context = context
        pref = EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editor = pref.edit()
    }

    fun putData(key: String, value: String) {
        editor!!.putString(key, value)
        editor!!.commit()
    }

    fun putData(key: String, value: Boolean) {
        editor!!.putBoolean(key, value)
        editor!!.commit()
    }

    fun getData(key: String): String {
        return pref.getString(key, "").toString()
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, true)
    }

    fun isLogin(): Boolean {
        return getData(sesId).isNotEmpty()
    }

    fun doLogin(data: DataUser?) {
        if (data != null) pref.edit {
            putString(sesId, data.sesId)
            putString(namaLengkap, data.namaLengkap)
            putString(groupMenuNama, data.groupMenuNama)
            putString(userTipeNomor, data.userTipeNomor)
            putString(img, data.img)
            putString(groupMenu, data.groupMenu)
        }

    }

    fun logout() {
        editor!!.clear()
        editor!!.commit()
    }
}