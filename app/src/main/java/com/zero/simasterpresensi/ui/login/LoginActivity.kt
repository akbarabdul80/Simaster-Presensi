package com.zero.simasterpresensi.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oratakashi.viewbinding.core.binding.activity.viewBinding
import com.oratakashi.viewbinding.core.tools.startActivity
import com.zero.simasterpresensi.data.model.commit_device.ResponseCommitDevice
import com.zero.simasterpresensi.data.model.user.DataUser
import com.zero.simasterpresensi.data.state.SimpleState
import com.zero.simasterpresensi.databinding.ActivityLoginBinding
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.ui.main.MainActivity
import com.zero.simasterpresensi.utils.MakeToast
import com.zero.simasterpresensi.utils.Validation.validateEditText
import dmax.dialog.SpotsDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

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
                            }else{
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