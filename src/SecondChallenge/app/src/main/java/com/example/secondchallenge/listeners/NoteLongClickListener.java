package com.example.secondchallenge.listeners;

import android.content.Context;
import android.view.View;


import androidx.fragment.app.FragmentManager;

import com.example.secondchallenge.activities.MainActivity;
import com.example.secondchallenge.fragments.NoteDialogFragment;

public class NoteLongClickListener implements View.OnLongClickListener {
    private final int clickedPosition;
    private final FragmentManager fragmentManager;

    public NoteLongClickListener(int clickedPosition, FragmentManager fragmentManager) {
        this.clickedPosition = clickedPosition;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public boolean onLongClick(View v) {
        NoteDialogFragment fragment = new NoteDialogFragment(this.clickedPosition);
        fragment.show(fragmentManager, "NoteDialog");
        return false;
    }
}
