package com.zero.simasterpresensi.ui.kkn

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oratakashi.viewbinding.core.binding.activity.viewBinding
import com.oratakashi.viewbinding.core.tools.onClick
import com.oratakashi.viewbinding.core.tools.toast
import com.zero.simasterpresensi.data.db.Sessions
import com.zero.simasterpresensi.data.state.SimpleState
import com.zero.simasterpresensi.databinding.ActivityKknactivityBinding
import com.zero.simasterpresensi.root.App
import com.zero.simasterpresensi.service.KknPresensiService
import com.zero.simasterpresensi.service.hapler.WorkerKKNPresensi
import com.zero.simasterpresensi.utils.MakeToast
import com.zero.simasterpresensi.utils.Validation.validateEditText
import dmax.dialog.SpotsDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit


class KKNActivity : AppCompatActivity(), LocationListener {
    private val binding: ActivityKknactivityBinding by viewBinding()
    private val viewModel: KKNViewModel by viewModel()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var locationManager: LocationManager

    private val locationPermissionCode = 2

    private val spotsDialogLocation: SpotsDialog by lazy {
        SpotsDialog(this, "Get Location...")
    }

    private val spotsDialog: SpotsDialog by lazy {
        SpotsDialog(this, "Mohon tunggu...")
    }

    private var longProcess = ""
    private var latProcess = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getLocation()
        setLongLatUI()
        setEditText()
        initAction()
        initObserver()
    }

    private fun initAction() {
        with(binding) {
            btnSaveFake.setOnClickListener {
                if (this@KKNActivity::location.isInitialized) {
                    etFakeLat.setText(location.latitude.toString())
                    etFakeLong.setText(location.longitude.toString())

                    setFakeLocation(location.longitude.toString(), location.latitude.toString())
                } else {
                    toast("Location not found")
                }
            }

            btnSaveCurrent.setOnClickListener {
                if (validateEditText(listOf(binding.etFakeLat, binding.etFakeLong))) {
                    setFakeLocation(
                        binding.etFakeLong.text.toString(),
                        binding.etFakeLat.text.toString()
                    )
                }
            }
            btnPresensi.setOnClickListener {
                MaterialAlertDialogBuilder(this@KKNActivity)
                    .setTitle("Konfirmasi")
                    .setMessage("Presensi menggunakan current location atau fake location?")
                    .setPositiveButton("Fake") { dialog, _ ->
                        latProcess = App.sessions.getData(Sessions.FAKE_LAT)
                        longProcess = App.sessions.getData(Sessions.FAKE_LONG)
                        doPresensi()
                    }.setNeutralButton("Current") { dialog, _ ->
                        latProcess = location.latitude.toString()
                        longProcess = location.longitude.toString()
                        doPresensi()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }

            btnStartService.onClick {
                if (validateEditText(listOf(binding.etFakeLat, binding.etFakeLong))) {
                    startService(0, 30)
                }
            }

            btnKillService.onClick {
                if (validateEditText(listOf(binding.etFakeLat, binding.etFakeLong))) {
                    killService()
                }
            }
        }
    }

    private fun doPresensi() {
        if (latProcess != "" && longProcess != "") {
            doPresensiInService()
            Log.e("TAG", "doPresensi: ${getCookies()}")
        } else {
            toast("Location not found")
        }
    }

    private fun getCookies(): String {
        val cookies = PreferenceManager.getDefaultSharedPreferences(this)
            .getStringSet("PREF_COOKIES", HashSet())!!
        var cookie = ""
        for (i in cookies) {
            cookie += "$i;"
        }
        return cookie
    }

    private fun setFakeLocation(long: String, lat: String) {
        App.sessions.putData(Sessions.FAKE_LAT, lat)
        App.sessions.putData(Sessions.FAKE_LONG, long)

        toast("Fake location saved")
    }

    private fun setEditText() {
        binding.etFakeLat.setText(App.sessions.getData(Sessions.FAKE_LAT))
        binding.etFakeLong.setText(App.sessions.getData(Sessions.FAKE_LONG))
    }

    private fun setLongLatUI() {
        if (this::location.isInitialized) {
            binding.tvLong.text = "Long : ${location.longitude}"
            binding.tvLat.text = "Lat : ${location.latitude}"
        }
    }

    private fun initObserver() {
        viewModel.stateMain.observe(this) { state ->
            state.let {
                when (it) {
                    is SimpleState.Loading -> {
                        spotsDialog.show()
                    }

                    is SimpleState.Result<*> -> {
                        viewModel.presensiKKN(
                            App.sessions.getData(Sessions.simasterUGM_cookie),
                            location.latitude.toString(),
                            location.longitude.toString()
                        )

                        spotsDialog.dismiss()
                    }

                    is SimpleState.Error -> {
                        spotsDialog.dismiss()
                        MakeToast.toastThrowable(this, it.error)
                    }
                }
            }
        }

        viewModel.stateKKN.observe(this) { state ->
            state.let {
                when (it) {
                    is SimpleState.Loading -> {
                        spotsDialog.show()
                    }

                    is SimpleState.Result<*> -> {
                        toast("Sukses presensi")
                        updateNotificationFromActivity(true)
                        spotsDialog.dismiss()
                    }

                    is SimpleState.Error -> {
                        spotsDialog.dismiss()
                        MakeToast.toastThrowable(this, it.error)
                        Log.e("TAG", "Error Presensi: ${it.error.cause}")
                    }
                }
            }
        }
    }

    private fun getLocation() {
        spotsDialogLocation.show()
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
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 5f, this
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 5f, this
            )
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    location = it
                    setLongLatUI()
                    Log.e("Location", "onLocationChanged: $it")
                    spotsDialogLocation.dismiss()
                } else {
                    Log.e("Location", "Last location is null")
                }
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
                            val resolvable = e as ResolvableApiException
                            resolvable.startResolutionForResult(
                                this,
                                100
                            )
                        } catch (exception: IntentSender.SendIntentException) {
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

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        showLocationSettingsDialog()
        doRequestPermission()
    }

    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        setLongLatUI()
        Log.e("Location", "onLocationChanged: $location")
        locationManager.removeUpdates(this)
        spotsDialogLocation.dismiss()
    }

    private fun updateNotificationFromActivity(apiCallSuccess: Boolean) {
        val intent = Intent(KknPresensiService.ACTION_UPDATE_NOTIFICATION)
        intent.putExtra("api_call_success", apiCallSuccess)
        sendBroadcast(intent)
    }

    private fun doPresensiInService() {
        val intent = Intent(KknPresensiService.ACTION_PRESENSI_NOW)
        sendBroadcast(intent)
    }

    private fun startService(hour: Int, minute: Int) {
        killService()
        val intent = Intent(this, KknPresensiService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        startService(intent)

        // Set the time for the request
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = hour
        cal[Calendar.MINUTE] = minute
        cal[Calendar.SECOND] = 0
        val currentTimeMillis = System.currentTimeMillis()
        var selectedTimeMillis = cal.timeInMillis

        // If the selected time is in the past, schedule it for the next day
        if (selectedTimeMillis <= currentTimeMillis) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
            selectedTimeMillis = cal.timeInMillis
        }
        val timeDiff = selectedTimeMillis - currentTimeMillis

        // Schedule the PeriodicWorkRequest with the calculated time difference
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            WorkerKKNPresensi::class.java, 1, TimeUnit.DAYS
        )
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag("periodicWorkRequest")
            .build()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }

    private fun killService() {
        WorkManager.getInstance(this).cancelAllWorkByTag(
            "periodicWorkRequest"
        )
        val intent = Intent(KknPresensiService.ACTION_KILL_SERVICE)
        sendBroadcast(intent)
    }

    private fun scheduleAlarm() {
        // Create a Calendar instance for 01:00 AM
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = 15
        calendar[Calendar.MINUTE] = 5
        calendar[Calendar.SECOND] = 0

        // If the scheduled time is in the past, set it for the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            toast("Alarm set for tomorrow at 01:00 AM")
        }

        // Create an Intent for the BroadcastReceiver
        val intent = Intent(KknPresensiService.ACTION_PRESENSI_NOW)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Get the AlarmManager service
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // Set the alarm to trigger at 01:00 AM every day
        alarmManager.setRepeating(
            AlarmManager.RTC,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


}