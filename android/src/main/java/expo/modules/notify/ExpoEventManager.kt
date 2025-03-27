package expo.modules.notify

import android.content.Context

object ExpoEventManager {
    private var reactContext: Context? = null
    private var cachedMessage: String? = null // 🔥 缓存 MQTT 消息

    fun init(reactContext: Context?) {
        this.reactContext = reactContext
        cachedMessage?.let { message ->
            sendMqttMessageEvent("notification", message)
            cachedMessage = null // 发送后清空缓存
        }
    }

    fun sendMqttMessageEvent(eventName: String, message: String) {
        if (reactContext == null) {
            cachedMessage = message // 🔥 RN 还未初始化，缓存消息
        } else {

            android.os.Handler().postDelayed({
                ExpoNotifyModule.instance?.sendMqttMessageEvent("notification", message)
            }, 500)

//            reactContext?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
//                ?.emit(eventName, message)
        }
    }

    fun sendNotificationClicked(eventName: String, message: String) {

    }
}