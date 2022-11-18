package com.example.secondchallenge.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.secondchallenge.util.NotesDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NotesViewModel extends ViewModel {
    private NotesDatabase database = null;
    private ArrayList<Note> notes = null;

    public void addNotes(Note... notes) {
        assert this.database != null;
        for (Note note : List.of(notes)) {
            this.notes.add(note);
            this.database.addNote(note);
        }
    }

    public void removeNotes(Note... notes) {
        assert this.database != null;
        for (Note note : List.of(notes)) {
            this.notes.remove(note);
            this.database.removeNote(note);
        }
    }

    public void updateNote(Note note, String title, String body, String origin) {
        String oldTitle = note.getTitle();
        note.setTitle(title);
        note.setBody(body);
        note.setOrigin(origin);
        this.database.updateNote(oldTitle, note);
    }

    public ArrayList<Note> getNotes() {
        if (this.notes == null) {
            assert this.database != null;
            this.notes = this.database.getNotes();
        }
        return this.notes;
    }

    public void setDatabase(NotesDatabase database) {
        this.database = database;
    }
}
