package expo.modules.notify.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import expo.modules.notify.service.MqttService

class RestartReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.mqttapp.RESTART_SERVICE") {
            Log.d("RestartReceiver", "收到重启 MQTT 服务的广播")
            val serviceIntent = Intent(context, MqttService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}