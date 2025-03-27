import { memo, useCallback, useEffect, useState } from "react";
import { View, Text, StyleSheet, Pressable, Linking } from "react-native";
import ExpoNotify from 'expo-notify';
import { useEvent } from "expo";

interface IProps_Example {}

const Example = memo((props: IProps_Example) => {
    // const [mqttMessage, setMqttMessage] = useState<string | null>(null);
    const topics = ['topic/1', 'topic/2', 'topic/3'];

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
    

    const onMqttMessage = useEvent(ExpoNotify, 'onMqttMessage');

    // // 监听 MQTT 消息
    // useEffect(() => {
    //     const subscription = ExpoNotify.addListener('onMqttMessage', (event) => {
    //         console.log("📩 Received MQTT message: ", event);
    //         setMqttMessage(event.message);
    //     });

    //     return () => {
    //         subscription.remove(); // 清理监听
    //     };
    // }, []);

    // 多个 MQTT 连接注册
    const registerTopic = useCallback((topic: string) => {
        const clientId = `client/${topic}`;
        console.log(`🚀 Registering topic: ${topic} with clientId: ${clientId}`);
        ExpoNotify.startMqttMultiService(
            'tcp://192.168.1.27:1883',
            clientId,
            topic,
        );
    }, []);

    // 多个 MQTT 连接注销
    const unregisterTopic = useCallback((topic: string) => {
        const clientId = `client/${topic}`;
        console.log(`🚀 Unregistering topic: ${topic}`);
        ExpoNotify.stopMqttMultiService(clientId);
    }, []);

    return (
        <View style={styles.container}>
            {/* MQTT 全局注册按钮 */}
            <Pressable style={styles.button} onPress={register}>
                <Text>Register</Text>
            </Pressable>
            <Pressable style={styles.button} onPress={unregister}>
                <Text>Unregister</Text>
            </Pressable>

            {/* 显示收到的 MQTT 消息 */}
            <View>
                {/* <Text>收到的消息：{mqttMessage || "暂无消息"}</Text> */}
                <Text>收到的消息：{onMqttMessage?.message || "暂无消息"}</Text>
            </View>

            {/* 遍历多个 topic */}
            {/* {topics.map((topic, index) => (
                <View key={index} style={styles.topicContainer}>
                    <Text>{topic}</Text>
                    <Pressable style={styles.button} onPress={() => registerTopic(topic)}>
                        <Text>Register</Text>
                    </Pressable>
                    <Pressable style={styles.button} onPress={() => unregisterTopic(topic)}>
                        <Text>Unregister</Text>
                    </Pressable>
                </View>
            ))} */}
        </View>
    );
});

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 10,
    },
    button: {
        width: 120,
        height: 40,
        borderWidth: 1,
        borderColor: 'black',
        borderRadius: 20,
        justifyContent: 'center',
        alignItems: 'center',
        marginVertical: 5,
        backgroundColor: '#f0f0f0',
    },
    topicContainer: {
        borderWidth: 1,
        borderColor: 'gray',
        padding: 10,
        marginVertical: 5,
        borderRadius: 8,
    },
});

export default Example;
