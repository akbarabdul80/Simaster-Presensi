package com.zero.simasterpresensi.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.ui.login.LoginActivity
import com.zero.simasterpresensi.ui.main.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class QrCodeScanTileService : TileService() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onClick() {
        super.onClick()
        if (App.sessions.isLogin()) {
            startActivity(Intent(this, MainActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        } else {
            startActivity(Intent(this, LoginActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }

}