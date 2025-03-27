## 安装
```
yarn add expo-modeles-core expo-notify@0.1.3 --registry=https://registry.npmjs.org/
```

## 配置
- AndroidManifest.xml, 配置深链地址
```
<activity android:name=".MainActivity" android:configChanges="keyboard|keyboardHidden|orientation|screenSize|screenLayout|uiMode" android:launchMode="singleTask" android:windowSoftInputMode="adjustResize" android:theme="@style/Theme.App.SplashScreen" android:exported="true" android:screenOrientation="portrait">
      <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
      </intent-filter>
      <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="chatapp"/> <-- 配置深链协议名
        <data android:scheme="https"     <-- 获取配置完整的深链地址
					android:host="chatapp.onelink.me" />
      </intent-filter>
    </activity>
```

- 消息格式
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
		"deeplink": "myapp://chat/message?message_id=msg_567890&sender_id=user_001"
	},
	"options": {
		"small_icon_id": "", <-- 主工程，drawable目录下的资源id
		"large_icon_id": "", <-- 主工程，drawable目录下的资源id
		"large_icon_url": "https://sns-avatar-qc.xhscdn.com/avatar/1040g2jo31avtpf5qmq6g5pq09qg080ucl7t6qt0?imageView2/2/w/60/format/webp|imageMogr2/strip" <-- 网络地址，大图标优先取网络地址
	},
	"ext": ""
}

```

## 使用
```
import { memo, useCallback, useEffect } from "react";
import { View, Text, Linking } from "react-native";
import ExpoNotify from 'expo-notify';
import { useEvent } from "expo";


interface IProps_HomePage {

}
const HomePage = memo((props: IProps_HomePage) => {

  useEffect(() => {
    Linking.getInitialURL().then((url) => {
      console.log("🚀 ~ 深链地址:", url)
    });
  }, []);

  useEffect(() => {
    register();
    return () => {
      unregister();
    }
  }, []);

  const onMqttMessage = useEvent(ExpoNotify, 'onMqttMessage');

  // 单个 MQTT 连接注册
  const register = useCallback(() => {
    ExpoNotify.startMqttService(
      'tcp://192.168.1.27:1883',
      'android_clientId',
      'test/response',
    );
  }, []);

  // 单个 MQTT 连接注销
  const unregister = useCallback(() => {
    ExpoNotify.stopMqttService();
  }, []);

  return (
    <View>
      <Text>这是主页</Text>
      <View>
        <Text>收到的消息：{onMqttMessage?.message || "暂无消息"}</Text>
      </View>
    </View>
  )
})


export default HomePage;
```
