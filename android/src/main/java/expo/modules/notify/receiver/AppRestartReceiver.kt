package expo.modules.notify.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import expo.modules.notify.service.MqttService

class AppRestartReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("MqttRestart", "应用被清理，重启 MQTT 服务")
        context.startForegroundService(Intent(context, MqttService::class.java))
    }
}