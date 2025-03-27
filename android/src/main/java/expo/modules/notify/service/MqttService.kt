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

class MqttService : Service() {

    private lateinit var mqttHelper: MqttHelper
//    private var isMqttInitialized = false // 防止重复初始化
    private var serverUri = "tcp://127.0.0.1:1883"
    private var clientId: String = "mqtt_android_clientId"
    private var topic: String = "test/response"

    @RequiresApi(Build.VERSION_CODES.O)
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

        // 获取启动参数
        intent?.extras?.let {
            serverUri = it.getString("serverUri", serverUri)
            clientId = it.getString("clientId", clientId)
            topic = it.getString("topic", topic)
        }
        // 防止 MQTT 连接重复初始化
        if (!this::mqttHelper.isInitialized) {
            initMqtt()
//            isMqttInitialized = true
        }
        return START_STICKY
    }

    private fun initMqtt() {
        if (serverUri.isEmpty() || clientId.isEmpty() || topic.isEmpty()) {
            Log.e("MqttService", "MQTT 配置未设置")
            return
        }
        // 初始化 MQTT
        mqttHelper = MqttHelper(this, serverUri, clientId)
        mqttHelper.setOnConnectedListener {
            mqttHelper.subscribeTopic(topic)
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

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MqttService", "MQTT 服务被销毁，尝试重启")

        mqttHelper.disconnect() // 释放 MQTT 资源
//        isMqttInitialized = false // 清除初始化标记，避免重复问题
//        mqttHelper = null

//        val broadcastIntent = Intent("com.example.mqttapp.RESTART_SERVICE")
//        sendBroadcast(broadcastIntent) // 发送广播，触发 `RestartReceiver`

//        val restartServiceIntent = Intent(applicationContext, MqttService::class.java)
//        startForegroundService(restartServiceIntent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("MqttService", "应用被清理，尝试重启 MQTT 服务")

//        val restartServiceIntent = Intent(applicationContext, MqttService::class.java).apply {
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


}