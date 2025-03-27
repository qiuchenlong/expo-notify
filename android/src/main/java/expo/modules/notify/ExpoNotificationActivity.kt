package expo.modules.notify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

class ExpoNotificationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent)
        finish() // 🔥 处理完后立即关闭
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val message = intent?.getStringExtra("mqtt_message")
        if (!message.isNullOrEmpty()) {
            Log.d("SDK", "SDK 收到 MQTT 消息: $message")
            // 🔥 发送事件给 React Native
//            ExpoNotifyModule.instance?.sendMqttMessageEvent("notification", message)
            ExpoEventManager.sendMqttMessageEvent("notification", message)

//            SDKEventManager.sendMqttMessageEvent("onNotificationClicked", message)
        }
        // 启动主工程的 MainActivity
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(launchIntent)
    }
}