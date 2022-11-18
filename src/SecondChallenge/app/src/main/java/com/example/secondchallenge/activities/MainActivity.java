package com.example.secondchallenge.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;

import com.example.secondchallenge.R;
import com.example.secondchallenge.listeners.FragmentChangeListener;
import com.example.secondchallenge.fragments.ListNotesFragment;
import com.example.secondchallenge.models.Note;
import com.example.secondchallenge.models.NotesViewModel;
import com.example.secondchallenge.util.NotesDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Open Database Connection
        NotesDatabase database = new NotesDatabase(this);

        // Init Shared View Model With Data
        NotesViewModel notesViewModel =
            new ViewModelProvider(this).get(NotesViewModel.class);
        notesViewModel.setDatabase(database);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Instantiate ListNotes Fragment
        replaceFragment(new ListNotesFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setTitle("Notes");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}