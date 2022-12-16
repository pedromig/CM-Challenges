package com.example.thirdchallenge.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.thirdchallenge.R;
import com.example.thirdchallenge.activities.MainActivity;
import com.example.thirdchallenge.models.DashboardViewModel;
import com.example.thirdchallenge.models.Measure;
import com.example.thirdchallenge.util.MQTT;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.slider.RangeSlider;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {

    public static final String LED_TOPIC = "/challenge/led";
    public static final String TEMPERATURE_TOPIC = "/challenge/temperature";
    public static final String HUMIDITY_TOPIC = "/challenge/humidity";

    private static final String CHANNEL_ID = "App";

    private MQTT mqttService;
    private DashboardViewModel viewModel;

    private SwitchCompat temperatureSwitch;
    private SwitchCompat humiditySwitch;

    private RangeSlider temperatureSlider;
    private RangeSlider humiditySlider;

    private LineChart chart;

    private ImageButton button;
    private boolean buttonState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        // Fetch Dashboard View Model
        this.viewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        // Fetch MqttService
        this.mqttService = setupMqttService();

        // Init Notification Channel
        createNotificationsChannel();

        // Led Button
        this.button = view.findViewById(R.id.image_button);
        this.buttonState = false;

        // Temperature Switch
        this.temperatureSwitch = view.findViewById(R.id.switch_temperature);
        this.temperatureSwitch.setChecked(true);

        // Humidity Switch
        this.humiditySwitch = view.findViewById(R.id.switch_humidity);
        this.humiditySwitch.setChecked(true);

        // Thresholds
        this.temperatureSlider = view.findViewById(R.id.slider_temperature);
        this.humiditySlider = view.findViewById(R.id.slider_humidity);

        // Charts
        this.chart = view.findViewById(R.id.chart);
        this.setupChart(this.chart);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup Listener For Arduino Led
        this.button.setOnClickListener(v -> {
            String msg = !this.buttonState ? "on" : "off";
            if (msg.equals("on")) {
                this.button.setImageResource(R.drawable.lightbulb_on);
            } else {
                this.button.setImageResource(R.drawable.lightbulb_off);
            }
            this.buttonState = !this.buttonState;
            System.out.println("Led State: " + msg);
            byte[] encodedPayload = msg.getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setQos(2);
            this.mqttService.mqttAndroidClient.publish(LED_TOPIC, message);
        });

        // Setup Listener For Temperature Switch
        this.temperatureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mqttService.subscribeToTopic(TEMPERATURE_TOPIC);
            } else {
                mqttService.unsubscribeTopic(TEMPERATURE_TOPIC);
            }
        });

        // Setup Listener For Humidity Switch
        this.humiditySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mqttService.subscribeToTopic(HUMIDITY_TOPIC);
            } else {
                mqttService.unsubscribeTopic(HUMIDITY_TOPIC);
            }
        });

        // Setup Threshold Temperature Slider
        this.temperatureSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            viewModel.setMaxTemperature(Collections.max(values));
            viewModel.setMinTemperature(Collections.min(values));
        });

        // Setup Humidity Temperature Slider
        this.humiditySlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            viewModel.setMaxHumidity(Collections.max(values));
            viewModel.setMinHumidity(Collections.min(values));
        });
    }

    private MQTT setupMqttService() {
        MQTT mqttService = new MQTT(requireContext(), "dashboard-app", "lol");
        mqttService.setCallback(new MqttCallbackExtended() {
            boolean notifyTemperature = true;
            boolean notifyHumidity = true;
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
                    if (notifyTemperature && (measure.getMeasure() < viewModel.getMinTemperature()
                            || measure.getMeasure() > viewModel.getMaxTemperature())) {
                        System.out.println("here");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("Temperature Warning!")
                                .setContentText("Measured temperature exceeded allowed range!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                        notificationManager.notify(1, builder.build());
                        notifyTemperature = false;
                    } else {
                        notifyTemperature = true;
                    }

                } else if (topic.equals(HUMIDITY_TOPIC)) {
                    viewModel.addHumidityMeasure(measure);
                    if (notifyTemperature && (measure.getMeasure() < viewModel.getMaxHumidity()
                            || measure.getMeasure() > viewModel.getMinHumidity())) {
                        System.out.println("here");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("Temperature Warning!")
                                .setContentText("Measured temperature exceeded allowed range!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                        notificationManager.notify(2, builder.build());
                        notifyTemperature = false;
                    } else {
                        notifyTemperature = true;
                    }
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

    private void setupChart(LineChart chart) {
        chart.setDrawGridBackground(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);
        chart.setBackgroundColor(Color.BLACK);
    }

    private void createNotificationsChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ThirdChallenge", importance);
        channel.setDescription("Third Challenge Notifications");
        NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}