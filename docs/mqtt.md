## mqtt server
- mac (https://juejin.cn/post/7394290549581430796)
```
// 安装
brew install mosquitto

// 检查
brew info mosquitto

// 配置
vim /opt/homebrew/etc/mosquitto/mosquitto.conf 
// 添加 allow_anonymous false 和 listen_port 1883

// 启动
mosquitto -c /opt/homebrew/etc/mosquitto/mosquitto.conf -v
/opt/homebrew/opt/mosquitto/sbin/mosquitto -c /opt/homebrew/etc/mosquitto/mosquitto.conf -v

```

```
// 订阅
mosquitto_sub -t "test/topic"
```

```
// 发布
mosquitto_pub -t "test/topic" -m "hello world"
```

```
// 发布
mosquitto_pub -t "test/response" -m "hello world"
```