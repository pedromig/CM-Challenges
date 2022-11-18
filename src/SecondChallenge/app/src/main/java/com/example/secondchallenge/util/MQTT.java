package com.example.secondchallenge.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.secondchallenge.fragments.MQTTPopupDialogFragment;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;


public class MQTT {
    public MqttAndroidClient mqttAndroidClient;

    private final String name;
    private final String server = "tcp://broker.hivemq.com:1883";
    private final String TAG = "MQTT";

    public MQTT(Context context, String name, String topic) {
        this.name = name;
        mqttAndroidClient = new MqttAndroidClient(context, server, name, Ack.AUTO_ACK);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                disconnectedBufferOptions.setBufferEnabled(true);
                disconnectedBufferOptions.setBufferSize(100);
                disconnectedBufferOptions.setPersistBuffer(false);
                disconnectedBufferOptions.setDeleteOldestMessages(false);
                mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w(TAG, "Failed to connect to: " + server + exception.toString());
            }
        });


    }

    public void stop() {
        mqttAndroidClient.disconnect();
    }

    public void subscribeToTopic(String topic) {
        if (topic != null && !topic.isEmpty()) {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast toast = Toast.makeText(mqttAndroidClient.getContext(),
                        "Subscriber to topic " + topic + "!",
                        Toast.LENGTH_SHORT
                    );
                    toast.show();
                    Log.w(TAG, "Subscribed! (topic = " + topic + " )");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast toast = Toast.makeText(mqttAndroidClient.getContext(),
                        "Subscription Failed Successfully",
                        Toast.LENGTH_SHORT
                    );
                    toast.show();
                    Log.w(TAG, "Subscribed fail!");
                }
            });
        }
    }

    public void unsubscribeTopic(String topic) {
        if (topic != null && !topic.isEmpty()) {
            mqttAndroidClient.unsubscribe(topic);
            Toast toast = Toast.makeText(mqttAndroidClient.getContext(),
                "Unsubscribed from topic" + topic + "!",
                Toast.LENGTH_SHORT
            );
            toast.show();
            Log.w(TAG, "Unsubscribed! (topic = " + topic + " )");
        }
    }
}

