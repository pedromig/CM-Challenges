package com.example.thirdchallenge.util;

import android.os.Handler;
import android.os.Looper;

import com.example.thirdchallenge.models.Measure;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncDataLoader {
    private final DB database;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public AsyncDataLoader(DB database) {
        this.database = database;
    }

    public interface Callback {
        void onComplete(ArrayList<Measure<Double>> temperatures, ArrayList<Measure<Double>> humidities);
    }

    public void executeAsync(Callback callback) {

        executor.execute(() -> {
            try {
                ArrayList<Measure<Double>> temperatures = this.database.getTemperatures();
                ArrayList<Measure<Double>> humidities = this.database.getHumidities();
                System.out.println("HERERERE");

                handler.post(() -> callback.onComplete(temperatures, humidities));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
