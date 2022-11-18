package com.example.secondchallenge.listeners;

import android.os.Bundle;
import android.view.View;

import com.example.secondchallenge.fragments.EditNoteFragment;

public class NoteClickListener implements View.OnClickListener {
    private final int clickedPosition;
    private final FragmentChangeListener fragmentChangeListener;

    public NoteClickListener(int clickedPosition, FragmentChangeListener fragmentChangeListener) {
        this.clickedPosition = clickedPosition;
        this.fragmentChangeListener = fragmentChangeListener;
    }

    @Override
    public void onClick(View v) {
        // Build Information Bundle for sharing state with Edit Notes Fragment
        Bundle bundle = new Bundle();
        bundle.putInt("selectedNote", clickedPosition);

        // Load new fragment with bundled information and switch to the other fragment
        EditNoteFragment fragment = new EditNoteFragment();
        fragment.setArguments(bundle);
        fragmentChangeListener.replaceFragment(fragment);
    }
}
