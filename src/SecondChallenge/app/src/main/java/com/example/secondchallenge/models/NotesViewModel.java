package com.example.secondchallenge.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.secondchallenge.util.NotesDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesViewModel extends ViewModel {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    private NotesDatabase database = null;
    private ArrayList<Note> notes = null;

    public void addNotes(Note... notes) {
        assert this.database != null;
        for (Note note : List.of(notes)) {
            this.notes.add(note);
            executor.execute(() -> this.database.addNote(note));
        }
    }

    public Note findNoteByTitle(String title) {
        for (Note note : this.notes) {
            if (note.getTitle().equals(title)) {
                return note;
            }
        }
        return null;
    }

    public void removeNotes(Note... notes) {
        assert this.database != null;
        for (Note note : List.of(notes)) {
            this.notes.remove(note);
            executor.execute(() ->this.database.removeNote(note));
        }
    }

    public void updateNote(Note note, String title, String body, String origin) {
        String oldTitle = note.getTitle();
        note.setTitle(title);
        note.setBody(body);
        note.setOrigin(origin);
        executor.execute(() -> this.database.updateNote(oldTitle, note));
    }

    public ArrayList<Note> getNotes() {
        // if (this.notes == null) {
        //     assert this.database != null;
        //     // TODO: Executor Here Too, Perphaps the most important
        //     this.notes = this.database.getNotes();
        // }
        return this.notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public void setDatabase(NotesDatabase database) {
        this.database = database;
    }
}
