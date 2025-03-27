package expo.modules.notify

import android.content.Intent
import android.util.Log
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.notify.service.MqttMultiService
import expo.modules.notify.service.MqttService

class ExpoNotifyModule : Module() {

//  private var serverUri: String = ""
//  private var clientId: String = ""
//  private var topic: String = ""

  init {
    instance = this
  }

  companion object {
    var instance: ExpoNotifyModule? = null
  }

  override fun definition() = ModuleDefinition {
    Name("ExpoNotify")

    /** 启动服务 **/
    Function("startMqttService") { serverUri: String, id: String, topic: String ->
      val context = requireNotNull(appContext.reactContext) { "Context is null" }
      val intent = Intent(context, MqttService::class.java).apply {
        putExtra("serverUri", serverUri)
        putExtra("clientId", id)  // 传递不同的 clientId
        putExtra("topic", topic)
      }
      context.startService(intent)
    }

    /** 停止服务 **/
    Function("stopMqttService") {
      val context = requireNotNull(appContext.reactContext) { "Context is null" }
      val intent = Intent(context, MqttService::class.java)
      context.stopService(intent)
    }

    /** 设置参数 **/
//    Function("setMqttConfig") {
//      serverUri = uri
//      clientId = id
//      topic = newTopic
//    }

    /** 启动多个Mqtt服务 **/
    Function("startMqttMultiService") { uri: String, id: String, topic: String ->
      val context = requireNotNull(appContext.reactContext) { "Context is null" }
      val intent = Intent(context, MqttMultiService::class.java).apply {
        putExtra("serverUri", uri)
        putExtra("clientId", id)  // 传递不同的 clientId
        putExtra("topic", topic)
      }
      context.startService(intent)
    }


    /** 停止多个Mqtt服务 **/
    Function("stopMqttMultiService") { id: String ->
      val context = requireNotNull(appContext.reactContext) { "Context is null" }
      val intent = Intent(context, MqttMultiService::class.java).apply {
        putExtra("clientId", id)  // 传递 clientId
        action = "STOP_MQTT_CLIENT"
      }
      context.startService(intent)  // 发送 Intent，让 Service 处理
    }

    /** 停止多个Mqtt服务 **/
    Function("stopMqttMultiAllService") {
      val context = requireNotNull(appContext.reactContext) { "Context is null" }
      val intent = Intent(context, MqttMultiService::class.java)
      context.stopService(intent)
    }


//    Function("onMessageReceived") { callback: (String, String) -> Unit ->
//      val context = requireNotNull(appContext.reactContext) { "Context is null" }
//      val service = MqttService()
//      service.setMessageCallback { topic, message ->
//        sendEvent("onMessageReceived", mapOf("topic" to topic, "message" to message))
//      }
//    }


    Events("onMqttMessage")


    Function("sendMqttMessageEvent", { topic: String, message: String ->
//      sendEvent("onMqttMessage", mapOf("topic" to topic, "message" to message))
      sendMqttMessageEvent(topic, message)
    })

    OnCreate {
      val reactContext = appContext.reactContext
      ExpoEventManager.init(reactContext)
    }

    OnNewIntent { it->
      Log.d("TAG", "definition: " + it.extras.toString())
    }

  }

  fun sendMqttMessageEvent(topic: String, message: String) {
    sendEvent("onMqttMessage", mapOf("topic" to topic, "message" to message))
    Log.d("ExpoNotifyModule", "发送 MQTT 事件: topic=$topic, message=$message")
  }

}
