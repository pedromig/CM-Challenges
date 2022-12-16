package com.example.thirdchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.thirdchallenge.R;
import com.example.thirdchallenge.fragments.DashboardFragment;
import com.example.thirdchallenge.fragments.FragmentChanger;
import com.example.thirdchallenge.models.DashboardViewModel;
import com.example.thirdchallenge.models.Measure;
import com.example.thirdchallenge.util.MQTT;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import kotlinx.coroutines.channels.Channel;

public class MainActivity extends AppCompatActivity implements FragmentChanger {

    public static final String LED_TOPIC = "/challenge/led";
    public static final String TEMPERATURE_TOPIC = "/challenge/temperature";
    public static final String HUMIDITY_TOPIC = "/challenge/humidity";

    private MQTT mqttService;
    private DashboardViewModel viewModel;

    private static final String CHANNEL_ID = "App";

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Shared View Model With Data
        this.viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Start Mqtt Service
        this.mqttService = setupMqttService();

        // Instantiate Display Dashboard Fragment
        replaceFragment(new DashboardFragment());

        // Init Notification Channel
        createNotificationsChannel();
    }

    private void createNotificationsChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ThirdChallenge", importance);
        channel.setDescription("Third Challenge Notifications");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    private MQTT setupMqttService() {
        MQTT mqttService = new MQTT(getApplicationContext(), "dashboard-app", "lol");
        mqttService.setCallback(new MqttCallbackExtended() {
            boolean notify = true;
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                System.err.println("CONNECTED: " + serverURI);
                // Default Subscribe Topic
                mqttService.subscribeToTopic(TEMPERATURE_TOPIC);
                mqttService.subscribeToTopic(HUMIDITY_TOPIC);
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.err.println("CONNECTION LOST!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Measure<Double> measure = new Measure(Double.parseDouble(message.toString()));
                if (topic.equals(TEMPERATURE_TOPIC)) {
                    viewModel.addTemperatureMeasure(measure);
                    if (notify && (measure.getMeasure() < viewModel.getMinTemperature()
                            || measure.getMeasure() > viewModel.getMaxTemperature())) {
                        System.out.println("here");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("Temperature Warning!")
                                .setContentText("Measured temperature exceeded allowed range!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(1, builder.build());
                        notify = false;
                    } else {
                        notify = true;
                    }

                } else if (topic.equals(HUMIDITY_TOPIC)) {
                    viewModel.addHumidityMeasure(measure);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("DELIVERED!");
            }
        });
        mqttService.connect();
        return mqttService;
    }

    public MQTT getMqttService() {
        return this.mqttService;
    }
}