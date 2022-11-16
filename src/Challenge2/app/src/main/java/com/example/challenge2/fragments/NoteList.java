package com.example.challenge2.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.challenge2.MainActivity;
import com.example.challenge2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteList extends Fragment {

    private FloatingActionButton addNoteBtn ;

    private FragmentChangeListener fragmentChangeListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        // Fetch Context for the Fragment Change listener
        this.fragmentChangeListener = (FragmentChangeListener) inflater.getContext();


        return view;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addNoteBtn = getView().findViewById(R.id.add_note_btn);
        addNoteBtn.setOnClickListener((v) -> {
        // Load new fragment with bundled information and switch to the other fragment
            CreateNote fragment = new CreateNote();
            fragmentChangeListener.replaceFragment(fragment);
        });
    }
}