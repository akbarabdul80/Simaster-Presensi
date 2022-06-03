package com.zero.simasterpresensi.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oratakashi.viewbinding.core.binding.activity.viewBinding
import com.oratakashi.viewbinding.core.tools.startActivity
import com.oratakashi.viewbinding.core.tools.toast
import com.zero.simasterpresensi.data.model.commit_device.ResponseCommitDevice
import com.zero.simasterpresensi.data.model.user.DataUser
import com.zero.simasterpresensi.data.state.SimpleState
import com.zero.simasterpresensi.databinding.ActivityLoginBinding
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.ui.main.MainActivity
import com.zero.simasterpresensi.utils.MakeToast
import com.zero.simasterpresensi.utils.RootUtils
import com.zero.simasterpresensi.utils.Validation.validateEditText
import dmax.dialog.SpotsDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.BufferedReader
import java.io.InputStreamReader

@SuppressLint("HardwareIds")
class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by viewBinding()
    private val viewModel: LoginViewModel by viewModel()
    private val aId: String by lazy {
        Settings.Secure.getString(application.contentResolver, "android_id")
    }
    private val spotsDialog: SpotsDialog by lazy {
        SpotsDialog(this, "Mohon tunggu...")
    }
    private lateinit var dataUser: DataUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListener()
        with(binding) {
            btnLogin.setOnClickListener {
                if (validateEditText(listOf(binding.etUsername, binding.etPassword))) doLogin()
            }
        }
        checkRoot()
    }

    private fun checkRoot() {
        if (RootUtils.isDeviceRooted) {
            val runtime = Runtime.getRuntime()
            val p =
                runtime.exec("su -c cat /data/data/id.ac.ugm.simaster/shared_prefs/SIMASTER.xml")
            val standardIn = BufferedReader(
                InputStreamReader(p.inputStream)
            )

            val response = standardIn
                .readText()
                .replace("&quot;", "")

            Log.e("Data", response)
            if (response != "") {
                val name = response
                    .split("<string name=\"user_nama_lengkap\">")[1]
                    .split("</string>")[0]

                val groupID = response
                    .split("<string name=\"group_menu\">")[1]
                    .split("</string>")[0]

                val sesId = response
                    .split("<string name=\"bearer\">")[1]
                    .split("</string>")[0]

                val userTypeNumber = response
                    .split("<string name=\"user_tipe_nomor\">")[1]
                    .split("</string>")[0]

                val img = response
                    .split("<string name=\"user_img\">")[1]
                    .split("</string>")[0]

                val groupMenuNama = response
                    .split("<string name=\"user_group_nama\">")[1]
                    .split("</string>")[0]

                dataUser = DataUser(
                    sesId,
                    name,
                    groupMenuNama,
                    userTypeNumber,
                    img,
                    groupID,
                    1,
                    ""
                )
                App.sessions.doLogin(dataUser)
                startActivity(MainActivity::class.java)
                toast("Device Rooted and Logged In with SIMASTER sessions")
                finish()
            } else {
                toast("Simaster not found or you are not logged in!")
            }
        }
    }

    private fun initListener() {
        viewModel.stateLogin.observe(this) { state ->
            state.let {
                when (it) {
                    is SimpleState.Loading -> {
                        spotsDialog.show()
                    }
                    is SimpleState.Result<*> -> {
                        spotsDialog.dismiss()
                        if (it.data is DataUser) {
                            dataUser = it.data
                            if (it.data.isLogin == 1) {
                                MaterialAlertDialogBuilder(this)
                                    .setTitle("Konfirmasi")
                                    .setMessage("Anda sudah login di perangkat ${it.data.device}, Apakah anda mau login didevice ini?")
                                    .setPositiveButton("Ya") { dialog, _ ->
                                        viewModel.commitDevice(it.data.sesId)
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton("Tidak") { dialog, _ ->
                                        dialog.dismiss()
                                    }.show()
                            } else {
                                App.sessions.doLogin(dataUser)
                                startActivity(MainActivity::class.java)
                                finish()
                            }
                        } else if (it.data is ResponseCommitDevice) {
                            if (it.data.status == 200) {
                                App.sessions.doLogin(dataUser)
                                startActivity(MainActivity::class.java)
                                finish()
                            }
                        }
                    }
                    is SimpleState.Error -> {
                        spotsDialog.dismiss()
                        MakeToast.toastThrowable(this, it.error)
                    }
                }
            }

        }
    }

    private fun doLogin() {
        viewModel.postLogin(
            aId,
            binding.etUsername.text.toString(),
            binding.etPassword.text.toString()
        )
    }

}