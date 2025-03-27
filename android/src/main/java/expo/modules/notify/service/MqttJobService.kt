package expo.modules.notify.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log

class MqttJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("MQTT", "JobService 触发，启动 MQTT")
        val intent = Intent(this, MqttService::class.java)
        startService(intent)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}