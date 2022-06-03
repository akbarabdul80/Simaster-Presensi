package com.zero.simasterpresensi.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.oratakashi.viewbinding.core.binding.activity.viewBinding
import com.oratakashi.viewbinding.core.tools.onClick
import com.oratakashi.viewbinding.core.tools.startActivity
import com.oratakashi.viewbinding.core.tools.toast
import com.zero.simasterpresensi.R
import com.zero.simasterpresensi.data.db.Sessions
import com.zero.simasterpresensi.data.model.scan_qr.ResponseScanQr
import com.zero.simasterpresensi.data.model.token.ResponseToken
import com.zero.simasterpresensi.data.state.SimpleState
import com.zero.simasterpresensi.databinding.ActivityMainBinding
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.ui.login.LoginActivity
import com.zero.simasterpresensi.utils.MakeToast
import com.zero.simasterpresensi.utils.RootUtils
import dmax.dialog.SpotsDialog
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream


class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler, LocationListener {
    private lateinit var mScannerView: ZXingScannerView
    private lateinit var locationManager: LocationManager
    private lateinit var easyImage: EasyImage

    private val binding: ActivityMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModel()

    private val spotsDialog: SpotsDialog by lazy {
        SpotsDialog(this, "Mohon tunggu...")
    }

    private val spotsDialogLocation: SpotsDialog by lazy {
        SpotsDialog(this, "Get Location...")
    }

    private val encodedAuth: String by lazy {
        "Basic " + Base64.encodeToString(
            resources.getString(R.string.auth_basic_qrcode).toByteArray(),
            Base64.NO_WRAP
        )
    }
    private val locationPermissionCode = 2
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var qrCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initScannerView()
        initEasyImage()
        initListener()

        if (RootUtils.isDeviceRooted) {
            toast("Device Rooted")
        } else {
            toast("Device Not Rooted")
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        with(binding) {
            btnLogout.onClick {
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah anda ingin keluar dari akun ini?")
                    .setPositiveButton("Ya") { dialog, _ ->
                        App.sessions.logout()
                        dialog.dismiss()
                        startActivity(LoginActivity::class.java)
                        finish()
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }

            cvFile.onClick {
                easyImage.openGallery(this@MainActivity)
            }
        }

        getLocation()
    }

    private fun initListener() {
        viewModel.stateMain.observe(this) { state ->
            state.let {
                when (it) {
                    is SimpleState.Loading -> {
                        spotsDialog.show()
                    }
                    is SimpleState.Result<*> -> {
                        when (it.data) {
                            is ResponseToken -> {
                                if (it.data.status == 200) {
                                    if (this::location.isInitialized) {
                                        viewModel.scanQr(
                                            encodedAuth,
                                            it.data.value!!,
                                            App.sessions.getData(Sessions.sesId),
                                            App.sessions.getData(Sessions.groupMenu),
                                            location.latitude.toString(),
                                            location.longitude.toString(),
                                            qrCode
                                        )
                                    } else {
                                        getLocation()
                                    }
                                }
                            }
                            is ResponseScanQr -> {
                                toast(it.data.message!!)
                            }
                        }

                        spotsDialog.dismiss()
                        mScannerView.startCamera()
                        mScannerView.setResultHandler(this)
                    }
                    is SimpleState.Error -> {
                        spotsDialog.dismiss()
                        MakeToast.toastThrowable(this, it.error)
                    }
                }
            }

        }
    }

    private fun initEasyImage() {
        easyImage = EasyImage.Builder(this)
            .setCopyImagesToPublicGalleryFolder(false)
            .setFolderName(resources.getString(R.string.app_name))
            .allowMultiple(false)
            .build()
    }

    private fun initScannerView() {
        mScannerView = ZXingScannerView(this)
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
        binding.flFlame.addView(mScannerView)
    }

    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScannerView()
                mScannerView.startCamera()
            } else {
                toast("Aplikasi tidak memiliki izin untuk akses kamera!")
                finish()
            }
        } else if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                toast("Aplikasi tidak memiliki izin untuk akses lokasi!")
            }
        }
    }


    override fun onPause() {
        mScannerView.stopCamera()
        super.onPause()
    }

    override fun handleResult(rawResult: Result?) {
        qrCode = rawResult!!.text
        viewModel.requestToken(encodedAuth)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {

                override fun onImagePickerError(
                    @NonNull error: Throwable,
                    @NonNull source: MediaSource
                ) {
                    error.printStackTrace()
                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    val inputStream: InputStream =
                        BufferedInputStream(FileInputStream(imageFiles[0].file))
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    scanQRImage(bitmap)
                }

                override fun onCanceled(@NonNull source: MediaSource) {
                }
            })
    }

    fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result: Result = reader.decode(bitmap)
            contents = result.text
            qrCode = contents
            viewModel.requestToken(encodedAuth)
        } catch (e: Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
            toast("QR-Code tidak ditemukan")
        }
        return contents
    }

    private fun getLocation() {
        spotsDialogLocation.show()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000, 5f, this
        )
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 5000, 5f, this
        )
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                location = it
                Log.e("Location", "onLocationChanged: $it")
                spotsDialogLocation.dismiss()
            } else {
                Log.e("Location", "Last location is null")
            }
        }
    }

    private fun showLocationSettingsDialog() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = e as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this,
                                100
                            )
                        } catch (exception: SendIntentException) {
                            // Ignore the error.
                        } catch (exception: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }


    override fun onLocationChanged(location: Location) {
        this.location = location
        Log.e("Location", "onLocationChanged: $location")
        locationManager.removeUpdates(this)
        spotsDialogLocation.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        showLocationSettingsDialog()
        mScannerView.startCamera()
        doRequestPermission()
    }

}