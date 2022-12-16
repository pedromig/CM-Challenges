package com.example.thirdchallenge.models;

import androidx.lifecycle.ViewModel;

import com.example.thirdchallenge.util.DB;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardViewModel extends ViewModel {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    private DB database = null;

    private final ArrayList<Measure<Double>> temperatures = new ArrayList<>();
    private final ArrayList<Measure<Double>> humidities = new ArrayList<>();

    private double minTemperature;
    private double maxTemperature;

    private double minHumidity;
    private double maxHumidity;

    public void addTemperatureMeasure(Measure<Double> measure) {
        // assert this.database != null;
        this.temperatures.add(measure);
        // executor.execute(() -> this.database.add(note));
    }

    public void addHumidityMeasure(Measure<Double> measure) {
        // assert this.database != null;
        this.humidities.add(measure);
        // executor.execute(() -> this.database.add(note));
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMinHumidity() {
        return minHumidity;
    }

    public double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMinHumidity(double minHumidity) {
        this.minHumidity = minHumidity;
    }

    public void setMaxHumidity(double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public ArrayList<Measure<Double>> getHumidities() {
        return humidities;
    }

    public ArrayList<Measure<Double>> getTemperatures() {
        return temperatures;
    }

    public void setDatabase(DB database) {
        this.database = database;
    }

}
