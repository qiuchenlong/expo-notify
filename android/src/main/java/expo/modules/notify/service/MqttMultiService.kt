package expo.modules.notify.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi

class MqttMultiService : Service() {

    private val mqttClients = mutableMapOf<String, MqttHelper>() // 管理多个 MQTT 连接

    private lateinit var mqttHelper: MqttHelper
    private var serverUri = "tcp://192.168.1.27:1883"
    private var clientId: String = "mqtt_android_clientId"
    private var topic: String = "test/response"

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG", "onCreate: mqttservice")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
//        } else {
//            startForeground(1, createNotification());
//        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG", "onStartCommand: Service started")

        if (intent?.action == "STOP_MQTT_CLIENT") {
            val clientId = intent.getStringExtra("clientId")
            if (clientId != null) {
                stopSpecificMqttClient(clientId)
                return START_STICKY
            }
        }

        // 获取启动参数
        intent?.extras?.let {
            serverUri = it.getString("serverUri", serverUri)
            clientId = it.getString("clientId", clientId)
            topic = it.getString("topic", topic)
        }

        // 如果该 clientId 连接已存在，则不重复初始化
        if (mqttClients.containsKey(clientId)) {
            Log.d("MqttService", "MQTT Client 已存在: $clientId")
            return START_STICKY
        }

        if (serverUri.isEmpty() || clientId.isEmpty() || topic.isEmpty()) {
            Log.e("MqttService", "MQTT 配置未设置")
            return START_STICKY
        }

        // 创建新的 MQTT 连接
        val mqttHelper = MqttHelper(this, serverUri, clientId).apply {
            setOnConnectedListener {
                subscribeTopic(topic)
            }
        }
        mqttClients[clientId] = mqttHelper // 存入 Map

        Log.d("MqttService", "MQTT Client 已启动: $clientId -> $serverUri 订阅 $topic")

        return START_STICKY
    }

    /** 断开指定 clientId 的 MQTT 连接 **/
    fun removeMqttClient(clientId: String) {
        mqttClients[clientId]?.let {
            it.disconnect()
            mqttClients.remove(clientId)
            Log.d("MqttService", "MQTT Client 已移除: $clientId")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "mqtt_service"
        val channel = NotificationChannel(channelId, "MQTT Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return Notification.Builder(this, channelId)
            .setContentTitle("MQTT 连接中...")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MqttService", "MQTT 服务被销毁，尝试重启")

        mqttClients.values.forEach { it.disconnect() } // 断开所有 MQTT 连接
        mqttClients.clear()

//        val broadcastIntent = Intent("com.example.mqttapp.RESTART_SERVICE")
//        sendBroadcast(broadcastIntent) // 发送广播，触发 `RestartReceiver`

//        val restartServiceIntent = Intent(applicationContext, MqttService::class.java)
//        startForegroundService(restartServiceIntent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("MqttService", "应用被清理，尝试重启 MQTT 服务")

//        val restartServiceIntent = Intent(applicationContext, MqttMultiService::class.java).apply {
//            setPackage(packageName)
//        }
//        val restartPendingIntent = PendingIntent.getService(
//            applicationContext,
//            1,
//            restartServiceIntent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        alarmManager.setExact(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            SystemClock.elapsedRealtime() + 1000,
//            restartPendingIntent
//        ) // 1 秒后重启

        super.onTaskRemoved(rootIntent)
    }

    /** 停止指定 clientId 的 MQTT 客户端 */
    private fun stopSpecificMqttClient(clientId: String) {
        mqttClients[clientId]?.disconnect()  // 断开连接
        mqttClients.remove(clientId)  // 从 Map 中移除
    }

}