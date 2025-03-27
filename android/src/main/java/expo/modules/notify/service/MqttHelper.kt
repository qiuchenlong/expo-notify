package expo.modules.notify.service

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import expo.modules.kotlin.AppContext
import expo.modules.notify.AppStateManager
import expo.modules.notify.ExpoNotifyModule
import expo.modules.notify.NotificationHelper
import expo.modules.notify.bean.PushNotification
import info.mqtt.android.service.Ack
//import org.eclipse.paho.android.service.MqttAndroidClient
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MqttHelper(private val context: Context, private val serverUri: String, private val clientId: String) {

    /**
     * MqttAndroidClient.Ack.AUTO_ACK: 收到消息后，立即自动确认
     * MqttAndroidClient.Ack.MANUAL_ACK: 收到消息后，通过执行MqttAndroidClient.acknowledgeMessage(String)，手动确认
     */
    private val mqttClient: MqttAndroidClient = MqttAndroidClient(context.applicationContext, serverUri, clientId, Ack.AUTO_ACK)
    private val handler = Handler(Looper.getMainLooper()) // 用于定时任务
    private val scheduler = Executors.newScheduledThreadPool(1) // 线程池
    private val notificationHelper = NotificationHelper(context) // 负责管理通知
    private val subscribedTopics = ConcurrentHashMap<String, Int>() // 存储订阅的主题和 QoS
    private var onConnectedListener: (() -> Unit)? = null

    fun setOnConnectedListener(listener: () -> Unit) {
        onConnectedListener = listener
    }

    init {
        connectMqtt()
    }

    // 连接Mqtt
    private fun connectMqtt() {
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true  // 允许自动重连
            isCleanSession = false       // 维持会话，避免订阅主题丢失
        }

        mqttClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "连接成功")
                onConnectedListener?.invoke() // 连接成功后回调
                reSubscribeTopics()  // 重新订阅之前的主题
                startPublishing() // 连接成功后，启动定时任务
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e("MQTT", "连接失败: ${exception?.message}")
                reconnectWithDelay()
            }
        })

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                Log.e("MQTT", "连接丢失: ${cause?.message}")
                reconnectWithDelay()
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.d("MQTT", "收到服务器消息: $topic -> ${message.toString()}")
                handleIncomingMessage(topic, message)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("MQTT", "消息发送成功")
            }
        })
    }

    // 断线重连
    private fun reconnectWithDelay() {
        Log.d("MQTT", "将在 5 秒后尝试重新连接...")
        handler.postDelayed({
            if (!mqttClient.isConnected) {
                connectMqtt()
            }
        }, 5000)
    }

    private fun handleIncomingMessage(topic: String, message: MqttMessage) {
        Log.d("MQTT", "MqttMessage: " + message.toString())
        if (AppStateManager.isAppInForeground(context)) {
            Log.d("MQTT", "应用在前台，不显示通知")
        } else {
//            notificationHelper.showNotification("新消息", message.toString())

            val gson = Gson()
            val notification = gson.fromJson(message.toString(), PushNotification::class.java)
            notificationHelper.showNotificationByDeepLink(topic, notification)
        }
        if (subscribedTopics.containsKey(topic)) {
            Log.d("MQTT", "处理订阅的主题: $topic")
            // 这里可以执行一些特定的业务逻辑，比如存储到数据库或触发某个回调
        } else {
            Log.w("MQTT", "收到未订阅的主题消息: $topic")
        }
        if (subscribedTopics.containsKey(topic)) {
            ExpoNotifyModule.instance?.sendMqttMessageEvent(topic, message.toString())
        }
    }

    // 定时发送 MQTT 消息
    private fun startPublishing() {
        scheduler.scheduleAtFixedRate({
            publishMessage("test/topic", "Hello MQTT! ${System.currentTimeMillis()}")
        }, 0, 5, TimeUnit.SECONDS) // 每 5 秒发送一次消息
    }

    // 订阅服务器的返回消息
    fun subscribeTopic(topic: String, qos: Int = 1) {
        if (!mqttClient.isConnected) {
            Log.w("MQTT", "MQTT 未连接，等待连接成功再订阅: $topic")
            handler.postDelayed({
                subscribeTopic(topic) // 递归等待连接后再订阅
            }, 1000) // 每 1 秒检查一次
            return
        }
        mqttClient.subscribe(topic, 1, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTT", "成功订阅主题: $topic")
                subscribedTopics[topic] = qos
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e("MQTT", "订阅失败: $topic, 错误: ${exception?.message}")
            }
        })
    }

    private fun reSubscribeTopics() {
        if (subscribedTopics.isNotEmpty()) {
            Log.d("MQTT", "重新订阅主题...")
            for ((topic, qos) in subscribedTopics) {
                subscribeTopic(topic, qos)
            }
        }
    }

    // 发布消息
    fun publishMessage(topic: String, message: String) {
        if (mqttClient.isConnected) {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = 1
            mqttClient.publish(topic, mqttMessage)
        } else {
            Log.e("MQTT", "无法发送消息，MQTT 未连接")
        }
    }

    fun disconnect() {
        try {
            if (mqttClient.isConnected) {
                mqttClient.disconnect()
            }
        } catch (e: Exception) {
            Log.e("MQTT", "断开连接异常: ${e.message}")
        } finally {
            // 释放资源，防止泄漏
            handler.removeCallbacksAndMessages(null)
            if (!scheduler.isShutdown) {
                scheduler.shutdownNow()
            }
            subscribedTopics.clear()
            mqttClient.unregisterResources()
        }
    }

}