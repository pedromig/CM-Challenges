package com.example.secondchallenge.listeners;

import androidx.appcompat.widget.SearchView;

import com.example.secondchallenge.adapters.NotesAdapter;

public class NoteFilterListener implements SearchView.OnQueryTextListener {
    private final NotesAdapter adapter;

    public NoteFilterListener(NotesAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.adapter.getFilter().filter(newText);
        return false;
    }
}
