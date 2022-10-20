package com.example.firstchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.firstchallenge.R;
import com.example.firstchallenge.fragments.DisplayAnimalFragment;
import com.example.firstchallenge.fragments.EditAnimalFragment;

public class MainActivity extends AppCompatActivity {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // Instantiate Display Animal Fragment
            getSupportFragmentManager().beginTransaction()
                                       .setReorderingAllowed(true)
                                       .add(R.id.fragment_display_animal,
                                           DisplayAnimalFragment.class,
                                           null
                                       ).commit();

            // Instantiate Edit Animal Fragment
            getSupportFragmentManager().beginTransaction()
                                       .setReorderingAllowed(true)
                                       .add(R.id.fragment_edit_animal,
                                           EditAnimalFragment.class,
                                           null
                                       ).commit();
        }
    }
}