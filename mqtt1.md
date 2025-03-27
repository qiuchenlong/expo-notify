## app前台
- 接收推送消息，监听消息

## app后台
- 接收推送消息，监听消息

## app返回
- 接收推送消息，deeplink

## app杀死
- 接收推送消息，deeplink

## 文本消息

```
{
    "title": "新消息",
    "body": "您有一条未读消息",
    "data": {
        "type": "text",
        "message_id": "msg_123456",
        "sender_id": "user_001",
        "sender_name": "Alice",
        "receiver_id": "user_002",
        "timestamp": 1711790000,
        "deeplink": "app://chat/message?message_id=msg_567890&sender_id=user_001"
    },
    "ext": ""
}
```
'''
{\"title\":\"新消息\",\"body\":\"您有一条未读消息\",\"data\":{\"type\":\"text\",\"message_id\":\"msg_123456\",\"sender_id\":\"user_001\",\"sender_name\":\"Alice\",\"receiver_id\":\"user_002\",\"timestamp\":1711790000,\"deeplink\":\"app://chat/message?message_id=msg_567890&sender_id=user_001\"},\"options\":{\"small_icon_id\":\"logo\",\"large_icon_id\":\"logo\",\"large_icon_url\":\"\"},\"ext\":\"\"}
'''

'''
{\"title\":\"新消息\",\"body\":\"您有一条未读消息\",\"data\":{\"type\":\"text\",\"message_id\":\"msg_123456\",\"sender_id\":\"user_001\",\"sender_name\":\"Alice\",\"receiver_id\":\"user_002\",\"timestamp\":1711790000,\"deeplink\":\"app://chat/message?message_id=msg_567890&sender_id=user_001\"},\"options\":{\"small_icon_id\":\"logo\",\"large_icon_id\":\"logo\",\"large_icon_url\":\"https://sns-avatar-qc.xhscdn.com/avatar/1040g2jo31avtpf5qmq6g5pq09qg080ucl7t6qt0?imageView2/2/w/60/format/webp|imageMogr2/strip\"},\"ext\":\"\"}
'''

