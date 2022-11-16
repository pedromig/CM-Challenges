package com.example.challenge2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.challenge2.R;
import com.example.challenge2.fragments.FragmentChangeListener;
import com.example.challenge2.fragments.CreateNote;
import com.example.challenge2.fragments.NoteList;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener{

    public MainActivity() {
        super(R.layout.activity_main);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Access Fragment Manager
        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();

        // Instantiate Notes List Fragment
        fm.setReorderingAllowed(true)
                .add(R.id.fragment_container, new NoteList())
                .commit();

    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

}
