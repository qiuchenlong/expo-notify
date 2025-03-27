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