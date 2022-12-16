package com.example.thirdchallenge.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.thirdchallenge.models.Measure;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db-home-dashboard.db";
    private static final int DATABASE_VERSION = 1;

    private static final String HUMIDITY_TABLE_NAME = "humidity";
    private static final String TEMPERATURE_TABLE_NAME = "temperature";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<Measure<Double>> getTemperatures() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");
        ArrayList<Measure<Double>> temperatures = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery("SELECT * FROM " + TEMPERATURE_TABLE_NAME, null)) {
                while (cursor.moveToNext()) {
                    Date date = df.parse(cursor.getString(1));
                    assert date != null;
                    temperatures.add(new Measure<>(
                                    cursor.getDouble(2),
                                    new Timestamp(date.getTime())
                            )
                    );
                }
            }
        }
        return temperatures;
    }

    public ArrayList<Measure<Double>> getHumidities() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");
        ArrayList<Measure<Double>> humidities = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery("SELECT * FROM " + HUMIDITY_TABLE_NAME, null)) {
                while (cursor.moveToNext()) {
                    Date date = df.parse(cursor.getString(1));
                    assert date != null;
                    humidities.add(new Measure<>(
                                    cursor.getDouble(2),
                                    new Timestamp(date.getTime())
                            )
                    );
                }
            }
        }
        return humidities;
    }

    private void addMeasure(Measure<Double> measure, String table) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("timestamp", df.format(measure.getTimestamp()));
            values.put("measure", measure.getMeasure());
            db.insert(table, null, values);
        }
    }

    public void addTemperature(Measure<Double> measure) {
        addMeasure(measure, TEMPERATURE_TABLE_NAME);
    }

    public void addHumidity(Measure<Double> measure) {
        addMeasure(measure, HUMIDITY_TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + HUMIDITY_TABLE_NAME + " (" +
                        "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "timestamp" + " TEXT," +
                        "measure" + " FLOAT " + ")"
        );
        db.execSQL(
                "CREATE TABLE " + TEMPERATURE_TABLE_NAME + " (" +
                        "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "timestamp" + " TEXT," +
                        "measure" + " TEXT" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HUMIDITY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TEMPERATURE_TABLE_NAME);
        onCreate(db);
    }
}
