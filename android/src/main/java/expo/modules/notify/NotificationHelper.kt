package expo.modules.notify

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import expo.modules.notify.bean.PushNotification

class NotificationHelper(private val context: Context) {
    private val channelId = "mqtt_notifications" // 通知渠道 ID
    private val notificationId = 1001 // 通知 ID

    init {
        createNotificationChannel() // 创建通知渠道
    }

    /** 创建通知渠道（适用于 Android 8.0+）*/
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MQTT 通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "MQTT 消息通知"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /** 显示通知 */
    fun showNotification(title: String, message: String) {
        val intent = Intent(context, ExpoNotificationActivity::class.java).apply {
//            setClassName("expo.modules.notify.example", "expo.modules.notify.example.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("mqtt_message", message) // 🔥 传递 MQTT 推送消息
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL) // 响铃 + 震动
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    /** 显示通知（深度链接） */
    fun showNotificationByDeepLink(topic: String, pushNotification: PushNotification) {
        val title = pushNotification.title
        val message = pushNotification.body
        val deeplink = pushNotification.data.deeplink
        val smallIconId = pushNotification.options.smallIconId?.takeIf { it.isNotEmpty() }
        val largeIconId = pushNotification.options.largeIconId?.takeIf { it.isNotEmpty() }
        val largeIconUrl = pushNotification.options.largeIconUrl?.takeIf { it.isNotEmpty() }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = deeplink.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 从 JSON 获取图标字段
        val iconName = smallIconId ?: "ic_android_logo"
        val iconResId = getIconResource(context, iconName)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL) // 响铃 + 震动
            .setContentIntent(pendingIntent)

        // **处理 largeIcon 逻辑**
        when {
            // **情况 1：使用网络图片**
            largeIconUrl != null -> {
                Glide.with(context)
                    .asBitmap()
                    .load(largeIconUrl)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            builder.setLargeIcon(resource)
                            val notification = builder.build()
                            val notificationManager =
                                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.notify(notificationId, notification)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }

            // **情况 2：largeIconUrl 为空但 largeIconId 存在，从资源文件加载**
            largeIconId != null -> {
                val largeIconResId = getIconResource(context, largeIconId)
                val largeIconBitmap = BitmapFactory.decodeResource(context.resources, largeIconResId)
                builder.setLargeIcon(largeIconBitmap)
                val notification = builder.build()
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, notification)
            }

            // **情况 3：largeIconUrl 和 largeIconId 都为空，直接发送通知**
            else -> {
                val notification = builder.build()
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, notification)
            }
        }

    }

    /** 获取Icon资源 */
    fun getIconResource(context: Context, iconName: String): Int {
        return context.resources.getIdentifier(iconName, "drawable", context.packageName)
    }

}