package com.example.secondchallenge.util;

import android.os.Handler;
import android.os.Looper;

import com.example.secondchallenge.models.Note;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncNoteLoader {
    private final NotesDatabase database;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public AsyncNoteLoader(NotesDatabase database) {this.database = database;}

    public interface Callback {
        void onComplete(ArrayList<Note> notes);
    }

    public void executeAsync(Callback callback) {
        executor.execute(() -> {
            ArrayList<Note> notes = this.database.getNotes();
            handler.post(() -> {
                callback.onComplete(notes);
            });
        });
    }
}
