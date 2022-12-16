package com.example.thirdchallenge.models;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public class Measure<T> {
    Date timestamp;
    T measure;

    public Measure(T measure) {
        this.measure = measure;
        this.timestamp = Timestamp.from(Instant.now());
    }

    public T getMeasure() {
        return measure;
    }

    @Override
    public String toString() {
        return "Measure{" +
                "timestamp=" + timestamp +
                ", measure=" + measure +
                '}';
    }
}
