package com.example.thirdchallenge.fragments;

import static com.example.thirdchallenge.activities.MainActivity.HUMIDITY_TOPIC;
import static com.example.thirdchallenge.activities.MainActivity.LED_TOPIC;
import static com.example.thirdchallenge.activities.MainActivity.TEMPERATURE_TOPIC;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.thirdchallenge.util.MQTT;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.slider.RangeSlider;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class DashboardFragment extends Fragment {

    private Switch temperatureSwitch;
    private Switch humiditySwitch;

    private MQTT mqttService;
    private LineChart chart;
    private RangeSlider slider;

    private boolean buttonState;
    private ImageButton button;

    private DashboardViewModel viewModel;

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
        this.mqttService = ((MainActivity) requireActivity()).getMqttService();

        this.temperatureSwitch = (Switch) view.findViewById(R.id.switch_temperature);
        this.temperatureSwitch.setChecked(true);

        this.humiditySwitch = (Switch) view.findViewById(R.id.switch_humidity);
        this.humiditySwitch.setChecked(true);

        // Led Button
        this.button = (ImageButton) view.findViewById(R.id.image_button);
        this.buttonState = false;

        // Threshold temperature
        this.slider = (RangeSlider) view.findViewById(R.id.slidertemp);

        // Threshold humidity
        this.slider = (RangeSlider) view.findViewById(R.id.sliderhum);

        // Charts
        this.chart = (LineChart) view.findViewById(R.id.chart);
        this.chart.setDrawGridBackground(true);
        this.chart.setTouchEnabled(true);
        this.chart.setDragEnabled(true);
        this.chart.setScaleEnabled(true);
        this.chart.setPinchZoom(false);
        this.chart.setBackgroundColor(Color.BLACK);

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
        // Setup Threshold Slider
        this.slider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            viewModel.setMaxTemperature(Collections.max(values));
            viewModel.setMinTemperature(Collections.min(values));
        });
    }


}