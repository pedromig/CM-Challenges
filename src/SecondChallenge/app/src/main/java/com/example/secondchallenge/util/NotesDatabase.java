package com.example.secondchallenge.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.secondchallenge.models.Note;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NotesDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db-notes.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "notes";
    private static final String TITLE_COLUMN = "title";
    private static final String BODY_COLUMN = "body";
    private static final String ORIGIN_COLUMN = "origin";

    public NotesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<Note> getNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
                while (cursor.moveToNext()) {
                    notes.add(new Note(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    ));
                }
            }
        }
        return notes;
    }

    public void addNote(Note note) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(TITLE_COLUMN, note.getTitle());
            values.put(BODY_COLUMN, note.getBody());
            values.put(ORIGIN_COLUMN, note.getOrigin());
            db.insert(TABLE_NAME, null, values);
        }
    }


    public void updateNote(String oldTitle, Note note) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(TITLE_COLUMN, note.getTitle());
            values.put(BODY_COLUMN, note.getBody());
            values.put(ORIGIN_COLUMN, note.getOrigin());
            db.update(TABLE_NAME, values, TITLE_COLUMN + " = ?",
                new String[]{ String.valueOf(oldTitle) });
        }
    }


    public void removeNote(Note note) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NAME, TITLE_COLUMN + "= ?",
                new String[]{ String.valueOf(note.getTitle()) }
            );
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_NAME + " (" +
                TITLE_COLUMN + " TEXT PRIMARY KEY," +
                BODY_COLUMN + " TEXT," +
                ORIGIN_COLUMN + " TEXT" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
