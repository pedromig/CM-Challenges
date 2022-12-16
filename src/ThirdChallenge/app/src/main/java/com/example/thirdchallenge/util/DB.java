package com.example.thirdchallenge.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db-home.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "home";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // public ArrayList<Note> getNotes() {
    //     ArrayList<Note> notes = new ArrayList<>();
    //     try (SQLiteDatabase db = this.getReadableDatabase()) {
    //         try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
    //             while (cursor.moveToNext()) {
    //                 notes.add(new Note(
    //                     cursor.getString(0),
    //                     cursor.getString(1),
    //                     cursor.getString(2)
    //                 ));
    //             }
    //         }
    //     }
    //     return notes;
    // }

    // public void addNote(Note note) {
    //     try (SQLiteDatabase db = this.getWritableDatabase()) {
    //         ContentValues values = new ContentValues();
    //         values.put(TITLE_COLUMN, note.getTitle());
    //         values.put(BODY_COLUMN, note.getBody());
    //         values.put(ORIGIN_COLUMN, note.getOrigin());
    //         db.insert(TABLE_NAME, null, values);
    //     }
    // }


    // public void updateNote(String oldTitle, Note note) {
    //     try (SQLiteDatabase db = this.getWritableDatabase()) {
    //         ContentValues values = new ContentValues();
    //         values.put(TITLE_COLUMN, note.getTitle());
    //         values.put(BODY_COLUMN, note.getBody());
    //         values.put(ORIGIN_COLUMN, note.getOrigin());
    //         db.update(TABLE_NAME, values, TITLE_COLUMN + " = ?",
    //             new String[]{ String.valueOf(oldTitle) });
    //     }
    // }


    // public void removeNote(Note note) {
    //     try (SQLiteDatabase db = this.getWritableDatabase()) {
    //         db.delete(TABLE_NAME, TITLE_COLUMN + "= ?",
    //             new String[]{ String.valueOf(note.getTitle()) }
    //         );
    //     }
    // }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        "x" + " TEXT PRIMARY KEY," +
                        "x" + " TEXT," +
                        "x" + " TEXT" + ")"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
