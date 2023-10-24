package com.zero.simasterpresensi.service.hapler

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zero.simasterpresensi.service.KknPresensiService

class WorkerKKNPresensi(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        doPresensiInService()
        return Result.success()
    }


    private fun doPresensiInService() {
        val intent = Intent(KknPresensiService.ACTION_PRESENSI_NOW)
        applicationContext.sendBroadcast(intent)
    }
}
