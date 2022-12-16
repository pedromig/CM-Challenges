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

import android.view.Gravity;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.slider.RangeSlider;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        List<Float> t = this.temperatureSlider.getValues();
        this.viewModel.setMinTemperature(Collections.min(t));
        this.viewModel.setMaxTemperature(Collections.max(t));

        this.humiditySlider = view.findViewById(R.id.slider_humidity);
        List<Float> h = this.humiditySlider.getValues();
        this.viewModel.setMinHumidity(Collections.min(h));
        this.viewModel.setMaxHumidity(Collections.max(h));

        // Charts
        this.chart = view.findViewById(R.id.chart);
        this.setupChart(container, this.chart);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set Chart Data
        updateChart();

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
                Measure<Double> measure = new Measure<>(Double.parseDouble(message.toString()));
                if (topic.equals(TEMPERATURE_TOPIC)) {
                    viewModel.addTemperatureMeasure(measure);
                    if (notifyTemperature && (measure.getMeasure() < viewModel.getMinTemperature()
                            || measure.getMeasure() > viewModel.getMaxTemperature())) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("Temperature Warning!")
                                .setContentText("Measured temperature not inside allowed range!")
                                .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                        notificationManager.notify(1, builder.build());
                        notifyTemperature = false;
                    } else if ((measure.getMeasure() < viewModel.getMinTemperature()
                            || measure.getMeasure() > viewModel.getMaxTemperature())) {
                        notifyTemperature = true;
                    }
                }

                if (topic.equals(HUMIDITY_TOPIC)) {
                    viewModel.addHumidityMeasure(measure);
                    if (notifyHumidity && (measure.getMeasure() < viewModel.getMinHumidity()
                            || measure.getMeasure() > viewModel.getMaxHumidity())) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle("Humidity Warning!")
                                .setContentText("Measured humidity not inside allowed range!")
                                .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                        notificationManager.notify(2, builder.build());
                        notifyHumidity = false;
                    } else if (measure.getMeasure() >= viewModel.getMinHumidity()
                            || measure.getMeasure() <= viewModel.getMaxHumidity()) {
                        notifyHumidity = true;
                    }
                }
                updateChart();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("DELIVERED!");
            }
        });
        mqttService.connect();
        return mqttService;
    }

    private void setupChart(ViewGroup view, LineChart chart) {
        // General Settings
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(true);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);
        chart.setDragEnabled(true);
        chart.getAxisRight().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setNoDataText("Turn on arduino to get some data");

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);

        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return df.format(Date.from(Instant.ofEpochMilli((long) value)));
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.setDrawGridLines(true);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setXOffset(10);
        l.setDrawInside(true);
    }

    private void updateChart() {

        this.chart.resetTracking();

        ArrayList<Entry> temperatures = new ArrayList<>();
        ArrayList<Entry> humidities = new ArrayList<>();

        for (Measure<Double> t : viewModel.getTemperatures()) {
            temperatures.add(new Entry(t.getTimestamp().getTime(), t.getMeasure().floatValue()));
        }

        for (Measure<Double> m : viewModel.getHumidities()) {
            humidities.add(new Entry(m.getTimestamp().getTime(), m.getMeasure().floatValue()));
        }

        LineDataSet temperaturesDataset = new LineDataSet(temperatures, "temperature");
        temperaturesDataset.setLineWidth(2.5f);
        temperaturesDataset.setCircleRadius(4f);
        temperaturesDataset.setColor(Color.rgb(222, 20, 20));
        temperaturesDataset.setCircleColor(Color.rgb(222, 20, 20));
        temperaturesDataset.enableDashedLine(10, 10, 0);
        temperaturesDataset.setLabel("Temperature");

        LineDataSet humiditiesDataset = new LineDataSet(humidities, "humidity");
        humiditiesDataset.setLineWidth(2.5f);
        humiditiesDataset.setCircleRadius(4f);
        humiditiesDataset.enableDashedLine(10, 10, 0);
        humiditiesDataset.setColor(Color.rgb(14, 148, 196));
        humiditiesDataset.setCircleColor(Color.rgb(14, 148, 196));
        humiditiesDataset.setLabel("Humidity");

        ArrayList<ILineDataSet> data = new ArrayList<>();
        data.add(temperaturesDataset);
        data.add(humiditiesDataset);

        this.chart.setData(new LineData(data));
        this.chart.invalidate();
    }

    private void createNotificationsChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ThirdChallenge", importance);
        channel.setDescription("Third Challenge Notifications");
        NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}