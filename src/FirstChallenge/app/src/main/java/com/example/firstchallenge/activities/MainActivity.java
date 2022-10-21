package com.example.firstchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.firstchallenge.R;
import com.example.firstchallenge.util.Animal;
import com.example.firstchallenge.fragments.DisplayAnimalFragment;
import com.example.firstchallenge.fragments.EditAnimalFragment;
import com.example.firstchallenge.fragments.FragmentChangeListener;
import com.example.firstchallenge.models.AnimalViewModel;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create Animals
        Animal frog = new Animal("Frog", "Pedro", 2, R.drawable.frog);
        Animal rhino = new Animal("Rhino", "Joana", 4, R.drawable.rhino);
        Animal snail = new Animal("Snail", "Josefina", 1, R.drawable.snail);

        // Init Shared Animal View Model With Data
        AnimalViewModel animalViewModel =
            new ViewModelProvider(this).get(AnimalViewModel.class);
        animalViewModel.addAnimals(frog, rhino, snail);

        // Access Fragment Manager
        FragmentManager fm = getSupportFragmentManager();

        // Instantiate Edit/Display Animal Fragments
        fm.beginTransaction()
          .setReorderingAllowed(true)
          .add(R.id.fragment_container, new EditAnimalFragment())
          .add(R.id.fragment_container, new DisplayAnimalFragment())
          .commit();
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}