package com.luomo.study.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by LiuMei on 2017-12-27.
 * <p>
 * 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
 */
public class ServerMQTT {

    /**
     * tcp://MQTT安装的服务器地址:MQTT定义的端口号
     */
//    public static final String HOST = "tcp://192.168.175.128:1883";
    public static final String HOST = "tcp://120.55.43.231:1883";
    /**
     * 定义一个主题
     */
    public static final String TOPIC = "news";
    /**
     * 定义MQTT的ID，可以在MQTT服务配置中指定
     */
    private static final String clientId = "server11";

    private MqttClient client;
    private MqttTopic topic;
    private String userName = "root";
    private String passWord = "WizInno2017";

    private MqttMessage message;

    /**
     * 构造函数
     *
     * @throws MqttException
     */
    public ServerMQTT() throws MqttException {
        // MemoryPersistence设置clientId的保存形式，默认为以内存保存
        client = new MqttClient(HOST, clientId, new MemoryPersistence());
        connect();
    }

    /**
     * 用来连接服务器
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new PushCallback());
            client.connect(options);

            topic = client.getTopic(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param topic
     * @param message
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public void publish(MqttTopic topic, MqttMessage message) throws MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! " + token.isComplete());
    }

    /**
     * 启动入口
     *
     * @param args
     * @throws MqttException
     */
    public static void main(String[] args) throws MqttException {
//        while (true) {
            ServerMQTT server = new ServerMQTT();
            server.message = new MqttMessage();
            server.message.setQos(1);
            server.message.setRetained(true);
            server.message.setPayload("hello,从java端发布一条消息".getBytes());
            server.publish(server.topic, server.message);
            System.out.println(server.message.isRetained() + "------retained状态");
//        }
    }
}
