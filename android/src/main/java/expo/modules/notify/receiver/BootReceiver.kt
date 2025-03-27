package expo.modules.notify.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import expo.modules.notify.service.MqttService

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("MQTT", "action=" + intent?.action)
        if (intent?.action == "com.huawei.mqtt.CONNECTED") {
            Log.d("MqttMonitor", "收到 MQTT 连接成功广播，启动自己的 MQTT")
            context?.startForegroundService(Intent(context, MqttService::class.java))
        }
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("MQTT", "设备开机，启动 MQTT 服务")
                startMqttService(context)
            }
            ConnectivityManager.CONNECTIVITY_ACTION -> {
                Log.d("MQTT", "网络状态变化，尝试重连 MQTT")
                startMqttService(context)
            }
            Intent.ACTION_SCREEN_ON -> {
                Log.d("MQTT", "屏幕亮起，启动 MQTT")
                startMqttService(context)
            }
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("MQTT", "屏幕关闭，维持 MQTT 运行")
                startMqttService(context)
            }
            Intent.ACTION_BATTERY_LOW -> {
                Log.d("MQTT", "电量低，优化 MQTT 连接")
                startMqttService(context)
            }
            Intent.ACTION_BATTERY_OKAY -> {
                Log.d("MQTT", "电量恢复正常，恢复 MQTT")
                startMqttService(context)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMqttService(context: Context) {
        val intent = Intent(context, MqttService::class.java)
        context.startForegroundService(intent)
    }
}