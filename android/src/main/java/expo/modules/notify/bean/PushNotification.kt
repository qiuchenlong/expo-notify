package expo.modules.notify.bean

import com.google.gson.annotations.SerializedName

// 定义外层 JSON 结构
data class PushNotification(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("data") val data: MessageData,
    @SerializedName("options") val options: OptionsData,
    @SerializedName("ext") val ext: String?
)


// 定义内部 Data 结构
data class MessageData(
    @SerializedName("type") val type: String,
    @SerializedName("message_id") val messageId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("receiver_id") val receiverId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("deeplink") val deeplink: String
)


// 定义 Options 结构
data class OptionsData(
    @SerializedName("small_icon_id") val smallIconId: String?,
    @SerializedName("large_icon_id") val largeIconId: String?,
    @SerializedName("large_icon_url") val largeIconUrl: String?,
)