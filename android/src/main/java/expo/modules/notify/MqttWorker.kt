package expo.modules.notify

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import expo.modules.notify.service.MqttService
import java.util.concurrent.TimeUnit

class MqttWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val serviceIntent = Intent(applicationContext, MqttService::class.java)
        applicationContext.startForegroundService(serviceIntent)
        return Result.success()
    }
}

fun scheduleMqttWork(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<MqttWorker>(1, TimeUnit.MINUTES)
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "MqttWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}